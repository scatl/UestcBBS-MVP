package com.scatl.uestcbbs.module.post.view;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.post.adapter.UserPostAdapter;
import com.scatl.uestcbbs.module.post.presenter.SelfPostPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class SelfPostActivity extends BaseActivity implements SelfPostView {

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private UserPostAdapter userPostAdapter;

    private SelfPostPresenter selfPostPresenter;

    public static final String TYPE_USER_POST = "topic";
    public static final String TYPE_USER_REPLY = "reply";
    public static final String TYPE_USER_FAVORITE = "favorite";

    private String type;
    private int page = 1;

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        type = intent.getStringExtra(Constant.IntentKey.TYPE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_self_post;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.self_post_toolbar);
        coordinatorLayout = findViewById(R.id.self_post_coor_layout);
        refreshLayout = findViewById(R.id.self_post_refresh);
        recyclerView = findViewById(R.id.self_post_rv);
    }

    @Override
    protected void initView() {
        selfPostPresenter = (SelfPostPresenter) presenter;

        if (type.equals(TYPE_USER_POST)) toolbar.setTitle(getString(R.string.my_post_title, 0));
        if (type.equals(TYPE_USER_REPLY)) toolbar.setTitle(getString(R.string.my_reply_title, 0));
        if (type.equals(TYPE_USER_FAVORITE)) toolbar.setTitle(getString(R.string.my_favorite_title, 0));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userPostAdapter = new UserPostAdapter(R.layout.item_simple_post);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(userPostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new SelfPostPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        userPostAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_card_view) {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, userPostAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        userPostAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_simple_post_board_name) {
                Intent intent = new Intent(this, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, userPostAdapter.getData().get(position).board_id);
                startActivity(intent);
            }
            if (view.getId() == R.id.item_simple_post_user_avatar) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, userPostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                selfPostPresenter.userPost(page,
                        ApiConstant.SIMPLE_POST_LIST_SIZE,
                        type, SelfPostActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                selfPostPresenter.userPost(page,
                        ApiConstant.SIMPLE_POST_LIST_SIZE,
                        type, SelfPostActivity.this);
            }
        });
    }

    @Override
    public void onGetUserPostSuccess(UserPostBean userPostBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (userPostBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (userPostBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (type.equals(TYPE_USER_POST)) toolbar.setTitle(getString(R.string.my_post_title, userPostBean.total_num));
        if (type.equals(TYPE_USER_REPLY)) toolbar.setTitle(getString(R.string.my_reply_title, userPostBean.total_num));
        if (type.equals(TYPE_USER_FAVORITE)) toolbar.setTitle(getString(R.string.my_favorite_title, userPostBean.total_num));

        if (userPostBean.page == 1) {
            userPostAdapter.setNewData(userPostBean.list);
            recyclerView.scheduleLayoutAnimation();
        } else userPostAdapter.addData(userPostBean.list);
    }

    @Override
    public void onGetUserPostError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showSnackBar(coordinatorLayout, msg);
    }
}
