package com.scatl.uestcbbs.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.widget.GrayFrameLayout;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scatl.util.ScreenUtil;
import com.scatl.util.TheftProofMark;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

public abstract class BaseActivity<P extends BasePresenter> extends BaseGrayActivity
        implements View.OnClickListener {

    private static final String TAG = "BaseActivity";

    public P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBar();

        super.onCreate(savedInstanceState);

        if (setLayoutRootView() != null) {
            setContentView(setLayoutRootView());
        } else if (setLayoutResourceId() != -1) {
            setContentView(setLayoutResourceId());
        }

        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_YES) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }

        if (setTheftProof()) {
            try {
                TheftProofMark
                        .getInstance()
                        .setTextSize(ScreenUtil.sp2px(this, 50f))
                        .setTextColor(getColor(R.color.theft_proof_color))
                        .show(this, String.valueOf(SharePrefUtil.getUid(this)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getIntent(getIntent());

        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);
        findView();
        initView();

        setOnRefreshListener();
        setOnItemClickListener();
    }

    protected int setLayoutResourceId() {
        return -1;
    }
    protected View setLayoutRootView(){
        return null;
    }
    protected abstract void findView();
    protected void initView() {
        View t = findViewById(R.id.toolbar);
        if (t instanceof Toolbar) {
            Toolbar toolbar = (Toolbar) t;
            toolbar.setNavigationOnClickListener(
                    v -> finish()
            );
            toolbar.setOnMenuItemClickListener(item -> {
                onOptionsSelected(item);
                return true;
            });
        }
    }
    protected abstract P initPresenter();
    protected void getIntent(Intent intent) {}
    protected void setOnRefreshListener() {}
    protected void setOnItemClickListener() {}
    protected void onClickListener(View view){}
    protected void onOptionsSelected(MenuItem item){}

    protected boolean setTheftProof() {
        return false;
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"), 0);
    }

    @Override
    public void onClick(View v) {
        onClickListener(v);
    }

    public void showToast(String msg, @ToastType String type) {
        ToastUtil.showToast(this, msg, type);
    }

    /**
     * author: sca_tl
     * description: Activity中的menu条目，在设置其showAsAction=”never”时，默认只显示文字title，
     * 而不会显示图标icon，可以在Activity中重写onMenuOpened()，通过反射使其图标可见。
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    protected boolean registerEventBus(){
        return false;
    }

    protected <T> void receiveEventBusMsg(BaseEvent<T> baseEvent) { }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEventBusReceived(BaseEvent<T> baseEvent){
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
