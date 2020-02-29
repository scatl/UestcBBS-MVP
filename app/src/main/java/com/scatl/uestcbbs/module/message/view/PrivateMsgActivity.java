package com.scatl.uestcbbs.module.message.view;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.message.adapter.PrivateMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.PrivateMsgPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;

import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class PrivateMsgActivity extends BaseActivity implements PrivateMsgView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PrivateMsgAdapter privateMsgAdapter;

    private PrivateMsgPresenter privateMsgPresenter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_private_msg;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.private_msg_toolbar);
        coordinatorLayout = findViewById(R.id.private_msg_coor_layout);
        refreshLayout = findViewById(R.id.private_msg_refresh);
        recyclerView = findViewById(R.id.private_msg_rv);
    }

    @Override
    protected void initView() {
        privateMsgPresenter = (PrivateMsgPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        privateMsgAdapter = new PrivateMsgAdapter(R.layout.item_private_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(privateMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new PrivateMsgPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        privateMsgAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_private_message_cardview) {
                Intent intent = new Intent(this, PrivateChatActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
                intent.putExtra(Constant.IntentKey.USER_NAME, privateMsgAdapter.getData().get(position).toUserName);
                startActivity(intent);
            }
        });

        privateMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            if (view.getId() == R.id.item_private_msg_user_icon) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
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
                privateMsgPresenter.getPrivateMsg(page, SharePrefUtil.getPageSize(PrivateMsgActivity.this), PrivateMsgActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                privateMsgPresenter.getPrivateMsg(page, SharePrefUtil.getPageSize(PrivateMsgActivity.this), PrivateMsgActivity.this);
            }
        });
    }

    @Override
    public void onGetPrivateMsgSuccess(PrivateMsgBean privateMsgBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (privateMsgBean.body.hasNext == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (privateMsgBean.body.hasNext == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (page == 1) {
            recyclerView.scheduleLayoutAnimation();
            privateMsgAdapter.setNewData(privateMsgBean.body.list);
        } else {
            privateMsgAdapter.addData(privateMsgBean.body.list);
        }
    }

    @Override
    public void onGetPrivateMsgError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        showSnackBar(coordinatorLayout, msg);
    }
}
