package com.scatl.uestcbbs.services.heartmsg.view;

import android.app.Service;
import android.os.Vibrator;

import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.base.BaseService;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.services.heartmsg.presenter.HeartMsgPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.NotificationUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HeartMsgService extends BaseService implements HeartMsgView{

    public static final String serviceName = "com.scatl.uestcbbs.services.heartmsg.view.HeartMsgService";

    private HeartMsgThread heartMsgThread;

    private boolean iAmGroot;

    public static int system_msg_count = 0;
    public static int at_me_msg_count = 0;
    public static int reply_me_msg_count = 0;
    public static int private_me_msg_count = 0;

    private HeartMsgPresenter heartMsgPresenter;

    @Override
    protected BasePresenter initPresenter() {
        return new HeartMsgPresenter();
    }

    @Override
    protected void initCommand() {
        super.initCommand();
        heartMsgPresenter = (HeartMsgPresenter) presenter;
        heartMsgThread = new HeartMsgThread();
        iAmGroot = true;
        heartMsgThread.start();
    }

    private class HeartMsgThread extends Thread {
        @Override
        public void run() {
            while (iAmGroot) {
                //getHeartMsg();
                heartMsgPresenter.getHeartMsg(SharePrefUtil.getToken(HeartMsgService.this),
                        SharePrefUtil.getSecret(HeartMsgService.this), Constant.SDK_VERSION);
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onGetHeartMsgSuccess(HeartMsgBean heartMsgBean) {
        if (heartMsgBean.body.replyInfo.count != 0 && heartMsgBean.body.replyInfo.count != reply_me_msg_count) {
            reply_me_msg_count = heartMsgBean.body.replyInfo.count;
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(new long[]{0, 90, 160, 90}, -1);
//            NotificationUtil.showNotification(HeartMsgService.this,
//                    BaseEvent.EventCode.NEW_REPLY_MSG,
//                    "10001",
//                    "new_reply",
//                    "新回复提醒",
//                    "你收到了" + heartMsgBean.body.replyInfo.count + "条新回复，点击查看");
        }

        if (heartMsgBean.body.atMeInfo.count != 0 && heartMsgBean.body.atMeInfo.count != at_me_msg_count) {
            at_me_msg_count = heartMsgBean.body.atMeInfo.count;
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(new long[]{0, 90, 160, 90}, -1);
//            NotificationUtil.showNotification(HeartMsgService.this,
//                    BaseEvent.EventCode.NEW_AT_MSG,
//                    "10010",
//                    "new_at",
//                    "新at提醒",
//                    "你收到了" + heartMsgBean.body.atMeInfo.count + "条at消息，点击查看");
        }

        if (heartMsgBean.body.pmInfos.size() != 0 && heartMsgBean.body.pmInfos.size() != private_me_msg_count) {
            private_me_msg_count = heartMsgBean.body.pmInfos.size();
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(new long[]{0, 90, 160, 90}, -1);

//            NotificationUtil.showNotification(HeartMsgService.this,
//                    BaseEvent.EventCode.NEW_PRIVATE_MSG,
//                    "10086",
//                    "new_private",
//                    "新私信提醒",
//                    "你收到了" + heartMsgBean.body.pmInfos.size() + "条新私信，点击查看");
        }

        if (heartMsgBean.body.systemInfo.count != 0 && heartMsgBean.body.systemInfo.count != system_msg_count) {
            system_msg_count = heartMsgBean.body.systemInfo.count;
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(new long[]{0, 90, 160, 90}, -1);
//            NotificationUtil.showNotification(HeartMsgService.this,
//                    BaseEvent.EventCode.NEW_SYSTEM_MSG,
//                    "10000",
//                    "new_private",
//                    "新系统消息提醒",
//                    "你收到了" + heartMsgBean.body.systemInfo.count + "条系统消息，点击查看");
        }

        //通知通知页面更新未读条数
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_MSG_COUNT));
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_AT_COUNT_ZERO) {
            at_me_msg_count = 0;
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_REPLY_COUNT_ZERO) {
            reply_me_msg_count = 0;
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_SYSTEM_MSG_ZERO) {
            system_msg_count = 0;
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_PRIVATE_COUNT_SUBTRACT) {
            if (private_me_msg_count != 0) {
                private_me_msg_count = private_me_msg_count - 1;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iAmGroot) iAmGroot = false;
    }
}
