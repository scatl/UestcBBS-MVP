package com.scatl.uestcbbs.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity
                            implements View.OnClickListener{

    private static final String TAG = "BaseActivity";

    public P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBar();

        super.onCreate(savedInstanceState);
        setContentView(setLayoutResourceId());

        if (SharePrefUtil.isNightMode(this)) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }

        getIntent(getIntent());

        presenter = initPresenter();
        if (presenter != null) presenter.addView(this);
        findView();
        initView();
        initSavedInstance(savedInstanceState);

        setOnRefreshListener();
        setOnItemClickListener();
    }

    protected abstract int setLayoutResourceId();
    protected abstract void findView();
    protected void initSavedInstance(Bundle savedInstanceState) {}
    protected abstract void initView();
    protected abstract P initPresenter();
    protected void getIntent(Intent intent) {}
    protected void setOnRefreshListener() {}
    protected void setOnItemClickListener() {}
    protected void onClickListener(View view){}


    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getColor(R.color.background_dark), 0);
    }

    @Override
    public void onClick(View v) {
        onClickListener(v);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean registerEventBus(){
        return false;
    }

    protected void receiveEventBusMsg(BaseEvent baseEvent) { }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusReceived(BaseEvent baseEvent){
        if (baseEvent != null) {
            receiveEventBusMsg(baseEvent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        //todo 加上这个会出现无响应的问题
        SubscriptionManager.getInstance().cancelAll();
        super.onDestroy();
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (presenter != null) presenter.detachView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确保子fragment调用onActivityResult方法
        getSupportFragmentManager().getFragments();
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
