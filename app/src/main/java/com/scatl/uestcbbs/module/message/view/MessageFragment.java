package com.scatl.uestcbbs.module.message.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.module.message.adapter.PrivateMsgAdapter;
import com.scatl.uestcbbs.module.message.presenter.MessagePresenter;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.greenrobot.eventbus.EventBus;

public class MessageFragment extends BaseFragment implements MessageView {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PrivateMsgAdapter privateMsgAdapter;

    private View headerView;
    private RelativeLayout systemMsgLayout, atMsgLayout, replyMsgLayout, dianPingMsgLayout;
    private TextView systemMsgCount, atMsgCount, replyMsgCount, dianPingMsgCount;

    private MessagePresenter messagePresenter;

    private int page = 1;

    public static MessageFragment getInstance(Bundle bundle) {
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void findView() {
        refreshLayout = view.findViewById(R.id.message_refresh);
        recyclerView = view.findViewById(R.id.message_rv);

        headerView = LayoutInflater.from(mActivity).inflate(R.layout.view_message_header, new LinearLayout(mActivity));
        atMsgCount = headerView.findViewById(R.id.message_at_msg_count);
        replyMsgCount = headerView.findViewById(R.id.message_reply_msg_count);
        systemMsgCount = headerView.findViewById(R.id.message_system_msg_count);
        atMsgLayout = headerView.findViewById(R.id.message_at_msg_rl);
        replyMsgLayout = headerView.findViewById(R.id.message_reply_msg_rl);
        systemMsgLayout = headerView.findViewById(R.id.message_system_msg_rl);
        dianPingMsgLayout = headerView.findViewById(R.id.message_dainping_msg_rl);
        dianPingMsgCount = headerView.findViewById(R.id.message_dainping_msg_count);
    }

    @Override
    protected void initView() {
        messagePresenter = (MessagePresenter) presenter;

        atMsgLayout.setOnClickListener(this);
        replyMsgLayout.setOnClickListener(this);
        systemMsgLayout.setOnClickListener(this);
        dianPingMsgLayout.setOnClickListener(this);

        setRecyclerViewListener();

        privateMsgAdapter = new PrivateMsgAdapter(R.layout.item_private_msg);
        privateMsgAdapter.addHeaderView(headerView, 0);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(privateMsgAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(0, 300, 1, false);
//        initUnreadMsg();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MessagePresenter();
    }

    @Override
    protected void onClickListener(View v) {
//        if (v.getId() == R.id.message_at_msg_rl) {
//            startActivity(new Intent(mActivity, AtMeMsgActivity.class));
//        }
//        if (v.getId() == R.id.message_reply_msg_rl) {
//            startActivity(new Intent(mActivity, ReplyMeMsgActivity.class));
//        }
        if (v.getId() == R.id.message_system_msg_rl) {
            startActivity(new Intent(mActivity, SystemMsgFragment.class));
        }
        if (v.getId() == R.id.message_dainping_msg_rl) {
            startActivity(new Intent(mActivity, DianPingMsgFragment.class));
        }
    }

    @Override
    protected void setOnItemClickListener() {
        privateMsgAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_private_message_cardview) {
                Intent intent = new Intent(mActivity, PrivateChatActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
                intent.putExtra(Constant.IntentKey.USER_NAME, privateMsgAdapter.getData().get(position).toUserName);
                startActivity(intent);
            }
        });

        privateMsgAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            messagePresenter.showDeletePrivateMsgDialog(mActivity,
                    privateMsgAdapter.getData().get(position).toUserName,
                    privateMsgAdapter.getData().get(position).toUserId,
                    position);
            return false;
        });

        privateMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_private_msg_user_icon) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                messagePresenter.getPrivateMsg(page, SharePrefUtil.getPageSize(mActivity), mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                messagePresenter.getPrivateMsg(page, SharePrefUtil.getPageSize(mActivity), mActivity);
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

        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onDeletePrivateMsgSuccess(String msg, int position) {
        try {
            privateMsgAdapter.getData().remove(position);
            privateMsgAdapter.notifyItemRemoved(position + privateMsgAdapter.getHeaderLayoutCount());
        } catch (Exception e) {
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
        ToastUtil.showToast(mActivity, msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onDeletePrivateMsgError(String msg) {
        ToastUtil.showToast(mActivity, msg, ToastType.TYPE_ERROR);
    }

    /**
     * author: sca_tl
     * description: 初始化未读消息数目
     */
//    private void initUnreadMsg() {
//        if (HeartMsgService.at_me_msg_count == 0) {
//            atMsgCount.setVisibility(View.GONE);
//        } else {
//            atMsgCount.setVisibility(View.VISIBLE);
//            atMsgCount.setText(String.valueOf(HeartMsgService.at_me_msg_count));
//        }
//
//        if (HeartMsgService.reply_me_msg_count == 0) {
//            replyMsgCount.setVisibility(View.GONE);
//        } else {
//            replyMsgCount.setVisibility(View.VISIBLE);
//            replyMsgCount.setText(String.valueOf(HeartMsgService.reply_me_msg_count));
//        }
//
//        if (HeartMsgService.system_msg_count == 0) {
//            systemMsgCount.setVisibility(View.GONE);
//        } else {
//            systemMsgCount.setVisibility(View.VISIBLE);
//            systemMsgCount.setText(String.valueOf(HeartMsgService.system_msg_count));
//        }
//
//        if (HeartMsgService.dianping_msg_count == 0) {
//            dianPingMsgCount.setVisibility(View.GONE);
//        } else {
//            dianPingMsgCount.setVisibility(View.VISIBLE);
//            dianPingMsgCount.setText(String.valueOf(HeartMsgService.dianping_msg_count));
//        }
//
//    }

    private void setRecyclerViewListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0));
            }
        });
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCESS
                || baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCESS) {
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
//            initUnreadMsg();
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.READ_PRIVATE_CHAT_MSG) {
            refreshLayout.autoRefresh(0, 300, 1, false);
        }
    }
}
