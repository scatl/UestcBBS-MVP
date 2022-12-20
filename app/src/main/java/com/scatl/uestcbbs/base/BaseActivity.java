package com.scatl.uestcbbs.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.scatl.uestcbbs.custom.GrayFrameLayout;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DownloadUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scatl.uestcbbs.util.WaterMark;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity
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

        if (SharePrefUtil.isNightMode(this)) {
            StatusBarUtil.setDarkMode(this);
        } else {
            StatusBarUtil.setLightMode(this);
        }

        if (setWaterMask()) {
            try {
                WaterMark
                        .getInstance()
                        .setTextColor(getColor(R.color.watermarkcolor))
                        .show(this, "UID:" + SharePrefUtil.getUid(this));
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

    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        if("FrameLayout".equals(name)){
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String attributeName = attrs.getAttributeName(i);
                String attributeValue = attrs.getAttributeValue(i);
                if (attributeName.equals("id")) {
                    int id = Integer.parseInt(attributeValue.substring(1));
                    String idVal = getResources().getResourceName(id);
                    if ("android:id/content".equals(idVal)) {
                        return new GrayFrameLayout(context, attrs);
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs);
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

    protected boolean setWaterMask() {
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

        if (requestCode == Constant.RequestCode.REQUEST_DOWNLOAD_PERMISSION && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Uri uriTree = data.getData();
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(uriTree, takeFlags);

                    SharePrefUtil.setDownloadFolderUri(this, uriTree.toString());
                    DownloadUtil.prepareDownload(this, SharePrefUtil.getDownloadFileName(this), SharePrefUtil.getDownloadFileUrl(this));
                } catch (Exception e) {
                    ToastUtil.showToast(this, "授权失败：" + e.getMessage(), ToastType.TYPE_ERROR);
                }
            }
        }
    }

}
