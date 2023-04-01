package com.scatl.uestcbbs.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseDialogFragment<P extends BasePresenter> extends DialogFragment
        implements View.OnClickListener{

    protected View view;
    protected Activity mActivity;
    public P presenter;
    private boolean isLoad = false;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = R.style.popwindow_anim;
            window.setAttributes(lp);
        }

        view = inflater.inflate(setLayoutResourceId(), container, false);

        getBundle(getArguments());
        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);
        findView();
        initView();
        setOnRefreshListener();
        setOnItemClickListener();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.getAttributes().setBlurBehindRadius(64);
            }
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //设置动画、位置、宽度等属性（必须放在onStart方法中）
        Window window = null;
        if (getDialog() != null) window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;
                window.setAttributes(attr);
                //设置背景，加入这句使界面水平填满屏幕
                window.setBackgroundDrawableResource(R.drawable.csu_shape_activity_round_corner);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    protected abstract int setLayoutResourceId();
    protected abstract void findView();
    protected abstract void initView();
    protected void getBundle(Bundle bundle) {}
    protected abstract P initPresenter();
    protected void onClickListener(View view){}
    protected void setOnItemClickListener() {}
    protected void setOnRefreshListener() {}
    protected boolean registerEventBus(){
        return false;
    }
    protected void lazyLoad(){}
    protected void receiveEventBusMsg(BaseEvent baseEvent) { }

    @Override
    public void onClick(View v) {
        onClickListener(v);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusReceived(BaseEvent baseEvent){
        if (baseEvent != null) {
            receiveEventBusMsg(baseEvent);
        }
    }

    public void showToast(String msg, @ToastType String type) {
        ToastUtil.showToast(mActivity, msg, type);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (presenter != null) presenter.detachView();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!isLoad) {
            isLoad = true;
            lazyLoad();
        }
    }
}
