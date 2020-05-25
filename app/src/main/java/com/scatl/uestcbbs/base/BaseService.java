package com.scatl.uestcbbs.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:54
 */
public abstract class BaseService<P extends BasePresenter> extends Service {

    public P presenter;

    protected abstract P initPresenter();
    protected void initCommand() {}
    protected boolean registerEventBus(){
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);

        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initCommand();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (presenter != null)presenter.detachView();
//        SubscriptionManager.getInstance().cancelAll();
    }
}
