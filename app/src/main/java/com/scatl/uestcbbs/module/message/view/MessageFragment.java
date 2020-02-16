package com.scatl.uestcbbs.module.message.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.message.presenter.MessagePresenter;
import com.scatl.uestcbbs.services.heartmsg.view.HeartMsgService;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class MessageFragment extends BaseFragment implements MessageView {

    private SmartRefreshLayout refreshLayout;
    private CardView systemMsgCard, atMsgCard, replyMsgCard, privateMsgCard;
    private TextView systemMsgCount, atMsgCount, replyMsgCount, privateMsgCount;

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
        systemMsgCard = view.findViewById(R.id.message_system_card);
        atMsgCard = view.findViewById(R.id.message_at_card);
        replyMsgCard = view.findViewById(R.id.message_reply_card);
        privateMsgCard = view.findViewById(R.id.message_private_card);
        systemMsgCount = view.findViewById(R.id.message_system_msg_count);
        atMsgCount = view.findViewById(R.id.message_at_msg_count);
        replyMsgCount = view.findViewById(R.id.message_reply_msg_count);
        privateMsgCount = view.findViewById(R.id.message_private_msg_count);
    }

    @Override
    protected void initView() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);

        systemMsgCard.setOnClickListener(this);
        atMsgCard.setOnClickListener(this);
        replyMsgCard.setOnClickListener(this);
        privateMsgCard.setOnClickListener(this);

        initUnreadMsg();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MessagePresenter();
    }

    @Override
    protected void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.message_system_card:  //系统通知
                Intent intent = new Intent(mActivity, SystemMsgActivity.class);
                startActivity(intent);
                break;

            case R.id.message_at_card:  //提到我的
                Intent intent1 = new Intent(mActivity, AtMeMsgActivity.class);
                startActivity(intent1);
                break;

            case R.id.message_reply_card:  //回复我的
                Intent intent2 = new Intent(mActivity, ReplyMeMsgActivity.class);
                startActivity(intent2);
                break;

            case R.id.message_private_card:  //私信
                Intent intent3 = new Intent(mActivity, PrivateMsgActivity.class);
                startActivity(intent3);
                break;

            default:
                break;

        }
    }

    /**
     * author: sca_tl
     * description: 初始化未读消息数目
     */
    private void initUnreadMsg() {
        systemMsgCount.setText(mActivity.getString(R.string.click_to_view));
        systemMsgCount.setTextColor(mActivity.getColor(R.color.text_color_dark));

        if (HeartMsgService.at_me_msg_count == 0) {
            atMsgCount.setText(mActivity.getString(R.string.no_new_message));
            atMsgCount.setTextColor(mActivity.getColor(R.color.text_color_dark));
        } else {
            atMsgCount.setText(mActivity.getResources()
                    .getString(R.string.new_at_message, HeartMsgService.at_me_msg_count));
            atMsgCount.setTextColor(mActivity.getColor(R.color.colorPrimary));
        }

        if (HeartMsgService.reply_me_msg_count == 0) {
            replyMsgCount.setText(mActivity.getString(R.string.no_new_message));
            replyMsgCount.setTextColor(mActivity.getColor(R.color.text_color_dark));
        } else {
            replyMsgCount.setText(mActivity
                    .getString(R.string.new_reply_message, HeartMsgService.reply_me_msg_count));
            replyMsgCount.setTextColor(mActivity.getColor(R.color.colorPrimary));
        }

        if (HeartMsgService.private_me_msg_count == 0) {
            privateMsgCount.setText(mActivity.getString(R.string.no_new_message));
            privateMsgCount.setTextColor(mActivity.getColor(R.color.text_color_dark));
        } else {
            privateMsgCount.setText(mActivity
                    .getString(R.string.new_private_message, HeartMsgService.private_me_msg_count));
            privateMsgCount.setTextColor(mActivity.getColor(R.color.colorPrimary));
        }


    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCESS
                || baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCESS) {
            //initView();
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
            initUnreadMsg();
        }
    }
}
