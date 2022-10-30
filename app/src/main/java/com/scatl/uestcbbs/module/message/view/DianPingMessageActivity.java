package com.scatl.uestcbbs.module.message.view;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.DianPingMessageBean;
import com.scatl.uestcbbs.module.message.adapter.DianPingMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.DianPingMsgPresenter;
import com.scatl.uestcbbs.module.post.view.ViewDianPingFragment;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

import java.util.IllegalFormatCodePointException;
import java.util.List;

public class DianPingMessageActivity extends BaseActivity<DianPingMsgPresenter> implements DianPingMessageView{

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private DianPingMsgAdapter dianPingMsgAdapter;

    private int page = 1;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_dian_ping_message;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.dianping_msg_coor_layout);
        toolbar = findViewById(R.id.dianping_msg_toolbar);
        refreshLayout = findViewById(R.id.dianping_msg_refresh);
        recyclerView = findViewById(R.id.dianping_msg_rv);
    }

    @Override
    protected void initView() {
        super.initView();
        dianPingMsgAdapter = new DianPingMsgAdapter(R.layout.item_dianping_msg);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(dianPingMsgAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top));

        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void setOnItemClickListener() {
        dianPingMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_dianping_msg_view_dianping_btn) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.IntentKey.TOPIC_ID, dianPingMsgAdapter.getData().get(position).tid);
                bundle.putInt(Constant.IntentKey.POST_ID, dianPingMsgAdapter.getData().get(position).pid);
                bundle.putBoolean(Constant.IntentKey.DATA_1, true);
                ViewDianPingFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
            }
        });
    }

    @Override
    protected DianPingMsgPresenter initPresenter() {
        return new DianPingMsgPresenter();
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                presenter.getDianPingMsg(page);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                presenter.getDianPingMsg(page);
            }
        });
    }

    @Override
    public void onGetDianPingMessageSuccess(List<DianPingMessageBean> dianPingMessageBeans, boolean hasNext) {
        if (hasNext) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore(true);
        } else {
            refreshLayout.finishRefreshWithNoMoreData();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }

        if (page == 1) {
            recyclerView.scheduleLayoutAnimation();
            dianPingMsgAdapter.setNewData(dianPingMessageBeans);
        } else {
            dianPingMsgAdapter.addData(dianPingMessageBeans);
        }

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_DIANPING_MSG_COUNT_ZERO));

    }

    @Override
    public void onGetDianPingMessageError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showToast(msg, ToastType.TYPE_ERROR);
    }
}