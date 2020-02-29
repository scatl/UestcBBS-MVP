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
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.message.adapter.AtMeMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.AtMeMsgPresenter;
import com.scatl.uestcbbs.module.post.view.PostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

public class AtMeMsgActivity extends BaseActivity implements AtMeMsgView{

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AtMeMsgAdapter atMeMsgAdapter;

    private AtMeMsgPresenter atMeMsgPresenter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_at_me_msg;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.at_me_msg_coor_layout);
        toolbar = findViewById(R.id.at_me_msg_toolbar);
        refreshLayout = findViewById(R.id.at_me_msg_refresh);
        recyclerView = findViewById(R.id.at_me_msg_rv);
    }

    @Override
    protected void initView() {

        atMeMsgPresenter = (AtMeMsgPresenter) presenter;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atMeMsgAdapter = new AtMeMsgAdapter(R.layout.item_at_me_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(atMeMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new AtMeMsgPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        atMeMsgAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_at_me_cardview) {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, atMeMsgAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        atMeMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_at_me_icon) {
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, atMeMsgAdapter.getData().get(position).user_id);
                startActivity(intent);
            }

            if (view.getId() == R.id.item_at_me_board_name) {
                Intent intent = new Intent(this, SingleBoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, atMeMsgAdapter.getData().get(position).board_id);
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
                atMeMsgPresenter.getAtMeMsg(page, SharePrefUtil.getPageSize(AtMeMsgActivity.this), AtMeMsgActivity.this);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                atMeMsgPresenter.getAtMeMsg(page, SharePrefUtil.getPageSize(AtMeMsgActivity.this), AtMeMsgActivity.this);
            }
        });
    }

    @Override
    public void onGetAtMeMsgSuccess(AtMsgBean atMsgBean) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            if (atMsgBean.has_next == 1) {
                refreshLayout.finishRefresh();
            } else {
                refreshLayout.finishRefreshWithNoMoreData();
            }
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            if (atMsgBean.has_next == 1) {
                refreshLayout.finishLoadMore(true);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }

        if (atMsgBean.page == 1) {
            recyclerView.scheduleLayoutAnimation();
            atMeMsgAdapter.setNewData(atMsgBean.body.data);
        } else {
            atMeMsgAdapter.addData(atMsgBean.body.data);
        }

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_AT_COUNT_ZERO));
    }

    @Override
    public void onGetAtMeMsgError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showSnackBar(coordinatorLayout, msg);
    }
}
