package com.scatl.uestcbbs.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/15 21:58
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment
                implements View.OnClickListener{
    protected View view;
    protected Activity mActivity;
    private boolean isLoad = false;

    public P presenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(setLayoutResourceId(), container, false);
        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);
        findView();
        initView();
        setOnRefreshListener();
        setOnItemClickListener();
        return view;
    }

    protected void getBundle(Bundle bundle) {}
    protected abstract int setLayoutResourceId();
    protected abstract void findView();
    protected abstract void initView();
    protected abstract P initPresenter();
    protected void setOnRefreshListener() {}
    protected void setOnItemClickListener() {}
    protected void onClickListener(View v){}

    protected void lazyLoad(){}

    @Override
    public void onClick(View v) {
        onClickListener(v);
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

    public void showToast(String msg, @ToastType String type) {
        ToastUtil.showToast(mActivity, msg, type);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
