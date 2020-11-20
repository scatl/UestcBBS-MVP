package com.scatl.uestcbbs.services;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.NotificationUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HeartMsgService extends Service {

    public static final String serviceName = "com.scatl.uestcbbs.services.HeartMsgService";

    private HeartMsgThread heartMsgThread;

    private boolean iAmGroot;

    public static int system_msg_count = 0;
    public static int at_me_msg_count = 0;
    public static int reply_me_msg_count = 0;
    public static int private_me_msg_count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        heartMsgThread = new HeartMsgThread();
        iAmGroot = true;
        try {
            heartMsgThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return START_STICKY;
    }

    private class HeartMsgThread extends Thread {
        @Override
        public void run() {
            while (iAmGroot) {
                getHeartMsg();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getHeartMsg(){
        new Retrofit.Builder()
                .baseUrl(ApiConstant.BBS_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiService.class)
                .getHeartMsg(Constant.SDK_VERSION, SharePrefUtil.getToken(HeartMsgService.this),
                        SharePrefUtil.getSecret(HeartMsgService.this))
                .enqueue(new Callback<HeartMsgBean>() {
                    @Override
                    public void onResponse(Call<HeartMsgBean> call, Response<HeartMsgBean> response) {
                        if (response.body() != null) {
                            try {
                                onGetHeartMsgSuccess(response.body());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<HeartMsgBean> call, Throwable t) { }
                });
    }

    public void onGetHeartMsgSuccess(HeartMsgBean heartMsgBean) {
        if (heartMsgBean.body.replyInfo.count != 0 && heartMsgBean.body.replyInfo.count != reply_me_msg_count) {
            reply_me_msg_count = heartMsgBean.body.replyInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_REPLY_MSG,
                    "10001",
                    "new_reply",
                    "新回复提醒",
                    "你收到了" + heartMsgBean.body.replyInfo.count + "条新回复，点击查看");
        }

        if (heartMsgBean.body.atMeInfo.count != 0 && heartMsgBean.body.atMeInfo.count != at_me_msg_count) {
            at_me_msg_count = heartMsgBean.body.atMeInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_AT_MSG,
                    "10010",
                    "new_at",
                    "新at提醒",
                    "你收到了" + heartMsgBean.body.atMeInfo.count + "条at消息，点击查看");
        }

        if (heartMsgBean.body.pmInfos.size() != 0 && heartMsgBean.body.pmInfos.size() != private_me_msg_count) {
            private_me_msg_count = heartMsgBean.body.pmInfos.size();
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_PRIVATE_MSG,
                    "10086",
                    "new_private",
                    "新私信提醒",
                    "你收到了" + heartMsgBean.body.pmInfos.size() + "条新私信，点击查看");
        }

        if (heartMsgBean.body.systemInfo.count != 0 && heartMsgBean.body.systemInfo.count != system_msg_count) {
            system_msg_count = heartMsgBean.body.systemInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_SYSTEM_MSG,
                    "10000",
                    "new_private",
                    "新系统消息提醒",
                    "你收到了" + heartMsgBean.body.systemInfo.count + "条系统消息，点击查看");
        }

        //通知通知页面更新未读条数
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_MSG_COUNT));
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
