package com.scatl.uestcbbs.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.GrayFrameLayout;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
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
        if (presenter != null) presenter.attachView(this);
        findView();
        initView();
        initSavedInstance(savedInstanceState);

        setOnRefreshListener();
        setOnItemClickListener();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
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

    protected abstract int setLayoutResourceId();
    protected abstract void findView();
    protected void initSavedInstance(Bundle savedInstanceState) {}
    protected abstract void initView();
    protected abstract P initPresenter();
    protected void getIntent(Intent intent) {}
    protected void setOnRefreshListener() {}
    protected void setOnItemClickListener() {}
    protected void onClickListener(View view){}
    protected void onOptionsSelected(MenuItem item){}
    protected int setMenuResourceId(){return 0;}


    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getColor(R.color.statusbar_color), 0);
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

    public void showLongSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (setMenuResourceId() != 0)getMenuInflater().inflate(setMenuResourceId(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        onOptionsSelected(item);
        return super.onOptionsItemSelected(item);
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
