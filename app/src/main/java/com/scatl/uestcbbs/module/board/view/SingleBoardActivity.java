package com.scatl.uestcbbs.module.board.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.module.board.adapter.SingleBoardAdapter;
import com.scatl.uestcbbs.module.board.adapter.TopTopicAdapter;
import com.scatl.uestcbbs.module.board.presenter.SingleBoardPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class SingleBoardActivity extends BaseActivity implements SingleBoardView{

    private Toolbar toolbar;
    private TextView hint;
    private ImageView classificationBtn;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView singleBoardRecyclerView;
    private SingleBoardAdapter singleBoardAdapter;

    private View topTopicView;
    private RecyclerView topTopicRecyclerView;
    private TopTopicAdapter topTopicAdapter;

    private SingleBoardPresenter singleBoardPresenter;

    private SingleBoardBean singleBoardBean;
    private int page, boardId, filterId;
    private String sortBy = "new";

    private static final String SORT_BY_NEW = "new"; //最新
    private static final String SORT_BY_ESSENCE = "essence";//精华
    private static final String SORT_BY_ALL = "all";//全部

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        boardId = intent.getIntExtra(Constant.IntentKey.BOARD_ID, Integer.MAX_VALUE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_single_board;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.single_board_toolbar);
        hint = findViewById(R.id.single_board_hint);
        classificationBtn = findViewById(R.id.single_board_classification_btn);
        refreshLayout = findViewById(R.id.single_board_refresh);
        singleBoardRecyclerView = findViewById(R.id.single_board_rv);

        topTopicView = LayoutInflater.from(this).inflate(R.layout.single_board_item_toptopic_view, new LinearLayout(this));
        topTopicRecyclerView = topTopicView.findViewById(R.id.single_board_item_toptopic_view_rv);

    }

    @Override
    protected void initView() {

        singleBoardPresenter = (SingleBoardPresenter) presenter;

        classificationBtn.setOnClickListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleBoardAdapter = new SingleBoardAdapter(R.layout.item_simple_post);
        singleBoardAdapter.addHeaderView(topTopicView, 0); //添加置顶帖view
        singleBoardRecyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        singleBoardRecyclerView.setAdapter(singleBoardAdapter);
        singleBoardRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top));
        singleBoardRecyclerView.scheduleLayoutAnimation();

        //置顶帖
        topTopicAdapter = new TopTopicAdapter(R.layout.item_toptopic);
        topTopicRecyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        topTopicRecyclerView.setAdapter(topTopicAdapter);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new SingleBoardPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.single_board_classification_btn) {
            if (singleBoardBean != null)
            singleBoardPresenter.showClassificationDialog(this,
                    singleBoardBean.classificationType_list, filterId);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        singleBoardAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_card_view) {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, singleBoardAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        singleBoardAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_user_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, singleBoardAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });

        topTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, topTopicAdapter.getData().get(position).id);
            startActivity(intent);
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                singleBoardPresenter.getSingleBoardPostList(page,
                        SharePrefUtil.getPageSize(SingleBoardActivity.this), 1,
                        boardId, filterId, "typeid", sortBy,
                        SingleBoardActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                singleBoardPresenter.getSingleBoardPostList(page,
                        SharePrefUtil.getPageSize(SingleBoardActivity.this), 1,
                        boardId, filterId, "typeid", sortBy,
                        SingleBoardActivity.this);
            }
        });
    }

    @Override
    public void onGetSingleBoardDataSuccess(SingleBoardBean singleBoardBean) {

        page = page + 1;
        toolbar.setTitle(singleBoardBean.forumInfo.title);
        hint.setText("");
        singleBoardRecyclerView.setVisibility(View.VISIBLE);

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (singleBoardBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (singleBoardBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (singleBoardBean.page == 1) {
            this.singleBoardBean = singleBoardBean;
            singleBoardRecyclerView.scheduleLayoutAnimation();
            singleBoardAdapter.addData(singleBoardBean.list, true);

            topTopicView.setVisibility(singleBoardBean.topTopicList.size() == 0 ? View.GONE : View.VISIBLE);
            topTopicAdapter.setNewData(singleBoardBean.topTopicList);

        } else {
            singleBoardAdapter.addData(singleBoardBean.list, false);
        }
    }

    @Override
    public void onGetSingleBoardDataError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
            hint.setText(msg);
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        if (msg.contains(ApiConstant.Code.RESPONSE_ERROR_500)){
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, ApiConstant.Post.BOARD_URL + boardId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClassificationSelected(int filterId) {
        page = 1;
        this.filterId = filterId;
        refreshLayout.autoRefresh(0, 300, 1, false);
    }
}
