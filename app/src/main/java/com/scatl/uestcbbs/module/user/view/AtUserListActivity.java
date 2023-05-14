package com.scatl.uestcbbs.module.user.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.module.search.adapter.SearchUserAdapter;
import com.scatl.uestcbbs.module.user.adapter.AtUserListAdapter;
import com.scatl.uestcbbs.module.user.presenter.AtUserListPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class AtUserListActivity extends BaseActivity<AtUserListPresenter> implements AtUserListView{

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private MaterialButton mSearchBtn;
    private AppCompatEditText mEditText;
    private TextView mHint;
    private AtUserListAdapter mAtUserListAdapter;
    private SearchUserAdapter mSearchUserAdapter;

    public static final int PAGE_SIZE = 2000;
    public static final int AT_USER_RESULT = 20;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_at_user_list;
    }

    @Override
    protected void findView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mEditText = findViewById(R.id.edittext);
        mSearchBtn = findViewById(R.id.search_btn);
        mHint = findViewById(R.id.hint);
    }

    @Override
    protected void initView() {
        super.initView();
        mSearchBtn.setOnClickListener(this);
        mAtUserListAdapter = new AtUserListAdapter(R.layout.item_at_user_list);
        mSearchUserAdapter = new SearchUserAdapter(R.layout.item_search_user, null);
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        mRefreshLayout.autoRefresh(10, 300, 1, false);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected AtUserListPresenter initPresenter() {
        return new AtUserListPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view == mSearchBtn) {
            mRefreshLayout.autoRefresh(0, 300, 1, true);
            presenter.searchUser(1, PAGE_SIZE,
                    mEditText.getText().toString()
                            .replaceAll(" ", "")
                            .replaceAll("\n", ""),
                    this);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        mSearchUserAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.IntentKey.AT_USER, "@" + mSearchUserAdapter.getData().get(position).name + " ");
            setResult(AT_USER_RESULT, intent);
            finish();
        });

        mAtUserListAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.IntentKey.AT_USER, "@" + mAtUserListAdapter.getData().get(position).name + " ");
            setResult(AT_USER_RESULT, intent);
            finish();
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, mRefreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                presenter.getAtUSerList(1, PAGE_SIZE);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
    }

    @Override
    public void onGetAtUserListSuccess(AtUserListBean atUserListBean) {
        mHint.setText(atUserListBean.total_num == 0 ? "啊哦，没有数据" : "");
        mRefreshLayout.finishRefresh();
        mRecyclerView.setAdapter(mAtUserListAdapter);
        mRecyclerView.scheduleLayoutAnimation();
        mAtUserListAdapter.setNewData(atUserListBean.list);
    }

    @Override
    public void onGetAtUserListError(String msg) {
        mRefreshLayout.finishRefresh();
        mHint.setText(msg);
        mSearchUserAdapter.setNewData(new ArrayList<>());
        mAtUserListAdapter.setNewData(new ArrayList<>());
    }

    @Override
    public void onSearchUserSuccess(SearchUserBean searchUserBean) {
        mHint.setText(searchUserBean.total_num == 0 ? "啊哦，没有数据" : "");
        mRefreshLayout.finishRefresh();
        mRecyclerView.setAdapter(mSearchUserAdapter);
        mRecyclerView.scheduleLayoutAnimation();
        mSearchUserAdapter.setNewData(searchUserBean.body.list);
    }

    @Override
    public void onSearchUserError(String msg) {
        mRefreshLayout.finishRefresh();
        mHint.setText(msg);
        mSearchUserAdapter.setNewData(new ArrayList<>());
        mAtUserListAdapter.setNewData(new ArrayList<>());
    }
}
