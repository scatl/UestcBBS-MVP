package com.scatl.uestcbbs.module.message.view;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.message.adapter.SystemMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.SystemMsgPresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

public class SystemMsgActivity extends BaseActivity implements SystemMsgView{

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SystemMsgAdapter systemMsgAdapter;

    private SystemMsgPresenter systemMsgPresenter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_system_msg;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.system_msg_toolbar);
        coordinatorLayout = findViewById(R.id.system_msg_coor_layout);
        refreshLayout = findViewById(R.id.system_msg_refresh);
        recyclerView = findViewById(R.id.system_msg_rv);
    }

    @Override
    protected void initView() {
        systemMsgPresenter = (SystemMsgPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        systemMsgAdapter = new SystemMsgAdapter( R.layout.item_system_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(systemMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new SystemMsgPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        systemMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_system_action_btn) {
                Intent intent = new Intent(SystemMsgActivity.this, WebViewActivity.class);
                intent.putExtra(Constant.IntentKey.URL, systemMsgAdapter.getData().get(position).actions.get(0).redirect);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_system_msg_user_icon) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, systemMsgAdapter.getData().get(position).user_id);
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
                systemMsgPresenter.getSystemMsg(page, SharePrefUtil.getPageSize(SystemMsgActivity.this), SystemMsgActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                systemMsgPresenter.getSystemMsg(page, SharePrefUtil.getPageSize(SystemMsgActivity.this), SystemMsgActivity.this);
            }
        });
    }

    @Override
    public void onGetSystemMsgSuccess(SystemMsgBean systemMsgBean) {

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (systemMsgBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (systemMsgBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (systemMsgBean.page == 1) {
            recyclerView.scheduleLayoutAnimation();
            systemMsgAdapter.setNewData(systemMsgBean.body.data);
        } else {
            systemMsgAdapter.addData(systemMsgBean.body.data);
        }

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_SYSTEM_MSG_ZERO));

    }

    @Override
    public void onGetSystemMsgError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }
        showSnackBar(coordinatorLayout, msg);
    }
}
