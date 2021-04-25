package com.scatl.uestcbbs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.GsonBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.api.ApiService;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.NotificationUtil;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
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
    public static int dianping_msg_count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
//        NotificationChannel mChannel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mChannel = new NotificationChannel("12345", "消息接收常驻通知",
//                    NotificationManager.IMPORTANCE_LOW);
//            notificationManager.createNotificationChannel(mChannel);
//            Notification notification =new NotificationCompat.Builder(getApplicationContext(),"12345")
//                    .setContentTitle("消息接收服务运行中")
//                    .setWhen(System.currentTimeMillis())
//                    .setSmallIcon(R.drawable.ic_notification_icon1)
//                    .build();
//            startForeground(12345, notification);
//        }
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
                getDianPingMsg();
                try {
                    sleep(7000);
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
                    public void onResponse(@NotNull Call<HeartMsgBean> call, @NotNull Response<HeartMsgBean> response) {
                        if (response.body() != null) {
                            try {
                                onGetHeartMsgSuccess(response.body());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<HeartMsgBean> call, @NotNull Throwable t) { }
                });
    }

    private void getDianPingMsg() {
        RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getDianPingMsg()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        try {
                            Document document = Jsoup.parse(response.body());
                            Elements elements = document.select("div[class=ct2_a wp cl]").select("ul[class=tb cl]").select("li");
                            for (int i = 0; i < elements.size(); i ++) {
                                if (elements.get(i).text().contains("点评")) {
                                    Matcher matcher = Pattern.compile("点评\\((.*?)\\)").matcher(elements.get(i).text());
                                    if (matcher.matches()) {
                                        dianping_msg_count = Integer.parseInt(matcher.group(1));
                                    }
                                    break;
                                }
                            }
                            onGetDianPingMsgSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) { }
                });
    }

    private void onGetHeartMsgSuccess(HeartMsgBean heartMsgBean) {
        if (heartMsgBean.body.replyInfo.count != 0 && heartMsgBean.body.replyInfo.count != reply_me_msg_count) {
            reply_me_msg_count = heartMsgBean.body.replyInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_REPLY_MSG,
                    "10001",
                    "新回复提醒",
                    "新回复提醒",
                    "你收到了" + heartMsgBean.body.replyInfo.count + "条新回复，点击查看");
        }

        if (heartMsgBean.body.atMeInfo.count != 0 && heartMsgBean.body.atMeInfo.count != at_me_msg_count) {
            at_me_msg_count = heartMsgBean.body.atMeInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_AT_MSG,
                    "10010",
                    "新at提醒",
                    "新at提醒",
                    "你收到了" + heartMsgBean.body.atMeInfo.count + "条at消息，点击查看");
        }

        if (heartMsgBean.body.pmInfos.size() != 0 && heartMsgBean.body.pmInfos.size() != private_me_msg_count) {
            private_me_msg_count = heartMsgBean.body.pmInfos.size();
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_PRIVATE_MSG,
                    "10086",
                    "新私信提醒",
                    "新私信提醒",
                    "你收到了" + heartMsgBean.body.pmInfos.size() + "条新私信，点击查看");
        }

        if (heartMsgBean.body.systemInfo.count != 0 && heartMsgBean.body.systemInfo.count != system_msg_count) {
            system_msg_count = heartMsgBean.body.systemInfo.count;
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_SYSTEM_MSG,
                    "10000",
                    "新系统消息提醒",
                    "新系统消息提醒",
                    "你收到了" + heartMsgBean.body.systemInfo.count + "条系统消息，点击查看");
        }

        //通知通知页面更新未读条数
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_MSG_COUNT));
    }

    private void onGetDianPingMsgSuccess() {
        if (dianping_msg_count != 0) {
            NotificationUtil.showNotification(HeartMsgService.this,
                    BaseEvent.EventCode.NEW_DAINPING_MSG,
                    "10110",
                    "新点评提醒",
                    "新点评提醒",
                    "你收到了" + dianping_msg_count + "条点评消息，点击查看");
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
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_DIANPING_MSG_COUNT_ZERO) {
            dianping_msg_count = 0;
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
