package com.scatl.uestcbbs.module.search.view;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.search.adapter.SearchPostAdapter;
import com.scatl.uestcbbs.module.search.adapter.SearchUserAdapter;
import com.scatl.uestcbbs.module.search.presenter.SearchPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class SearchActivity extends BaseActivity<SearchPresenter> implements SearchView, View.OnKeyListener {

    private Toolbar toolbar;
    private RadioButton byPost, byUser;
    private AppCompatEditText keyWord;
    private Button searchBtn;
    private TextView hint;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SearchUserAdapter searchUserAdapter;
    private SearchPostAdapter searchPostAdapter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        byPost = findViewById(R.id.search_radio_btn_by_post);
        byUser = findViewById(R.id.search_radio_btn_by_user);
        keyWord = findViewById(R.id.search_keyword_edittext);
        refreshLayout = findViewById(R.id.search_refresh);
        recyclerView = findViewById(R.id.search_rv);
        searchBtn = findViewById(R.id.search_btn);
        hint = findViewById(R.id.search_hint);
    }

    @Override
    protected void initView() {
        super.initView();

        keyWord.setOnKeyListener(this);
        searchBtn.setOnClickListener(this);

        CommonUtil.showSoftKeyboard(this, keyWord, 1);

        searchPostAdapter = new SearchPostAdapter(R.layout.item_simple_post, SharePrefUtil.isHideAnonymousPost(this));
        searchUserAdapter = new SearchUserAdapter(R.layout.item_search_user);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
    }

    @Override
    protected SearchPresenter initPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected void onClickListener(View view) {

        if (view.getId() == R.id.search_btn) {
            CommonUtil.hideSoftKeyboard(SearchActivity.this, keyWord);
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
    }

    private void startSearch() {
        if (byPost.isChecked()) {
            presenter.searchPost(page, SharePrefUtil.getPageSize(SearchActivity.this), keyWord.getText().toString(), this);
        } else {
            presenter.searchUser(page, SharePrefUtil.getPageSize(SearchActivity.this),
                    keyWord.getText().toString().replaceAll(" ", "").replaceAll("\n", ""),this);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        searchPostAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, searchPostAdapter.getData().get(position).topic_id);
            startActivity(intent);
        });

        searchPostAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Intent intent = new Intent(SearchActivity.this, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, searchPostAdapter.getData().get(position).user_id);
            startActivity(intent);
        });

        searchUserAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(SearchActivity.this, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, searchUserAdapter.getData().get(position).uid);
            startActivity(intent);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                startSearch();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                startSearch();
            }
        });
    }

    @Override
    public void onSearchUserSuccess(SearchUserBean searchUserBean) {
        page = page + 1;
        hint.setText("");
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (searchUserBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (searchUserBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (searchUserBean.page == 1) {
            recyclerView.setAdapter(searchUserAdapter);
            recyclerView.scheduleLayoutAnimation();
            searchUserAdapter.setNewData(searchUserBean.body.list);
        } else {
            searchUserAdapter.addData(searchUserBean.body.list);
        }
        if (searchUserAdapter.getData().size() == 0) hint.setText("啊哦，没有数据");
    }

    @Override
    public void onSearchUserError(String msg) {
        hint.setText(msg);
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
    }

    @Override
    public void onSearchPostSuccess(SearchPostBean searchPostBean) {
        page = page + 1;
        hint.setText("");
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (searchPostBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (searchPostBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (searchPostBean.page == 1) {
            recyclerView.setAdapter(searchPostAdapter);
            recyclerView.scheduleLayoutAnimation();
            searchPostAdapter.addSearchPostData(searchPostBean.list, true);
        } else {
            searchPostAdapter.addSearchPostData(searchPostBean.list, false);
        }

        if (searchPostAdapter.getData().size() == 0) hint.setText("啊哦，没有数据");
    }

    @Override
    public void onSearchPostError(String msg) {
        hint.setText(msg);
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            CommonUtil.hideSoftKeyboard(SearchActivity.this, v);
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
        return false;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(
                this,
                ColorUtil.getAttrColor(this, R.attr.colorSurface), 0);
    }
}
