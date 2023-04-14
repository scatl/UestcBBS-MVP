package com.scatl.uestcbbs.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetDialog;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * author: sca_tl
 * description:
 * date: 2019/12/1 17:09
 */
public abstract class BaseBottomFragment<P extends BasePresenter> extends ViewPagerBottomSheetDialogFragment
        implements View.OnClickListener {

    public ViewPagerBottomSheetBehavior mBehavior;
    protected View view;
    protected Activity mActivity;
    public P presenter;

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
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewPagerBottomSheetDialog bottomSheetDialog = (ViewPagerBottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = View.inflate(getContext(), setLayoutResourceId(), null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getDelegate()
                .findViewById(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundResource(R.drawable.csu_shape_activity_round_corner);
        mBehavior = ViewPagerBottomSheetBehavior.from((View) view.getParent());

//        mBehavior.setDraggable(false);
//        mBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * setMaxHeightMultiplier()));

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * setMaxHeightMultiplier());
        view.setLayoutParams(layoutParams);

        getBundle(getArguments());
        presenter = initPresenter();
        if (presenter != null) presenter.attachView(this);
        findView();
        initView();
        setOnRefreshListener();
        setOnItemClickListener();

        return bottomSheetDialog;
    }


    protected abstract int setLayoutResourceId();
    protected abstract void findView();
    protected abstract void initView();
    protected double setMaxHeightMultiplier() {
        return 0.8;
    }
    protected void getBundle(Bundle bundle) {}
    protected abstract P initPresenter();
    protected void setOnRefreshListener() {}
    protected void setOnItemClickListener() {}
    protected void onClickListener(View view){}
    protected boolean registerEventBus(){
        return false;
    }
    protected void receiveEventBusMsg(BaseEvent baseEvent) { }
    @Override
    public void onClick(View v) {
        onClickListener(v);
    }

    public void showToast(String msg, @ToastType String type) {
        ToastUtil.showToast(mActivity, msg, type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确保子fragment调用onActivityResult方法
        getChildFragmentManager().getFragments();
        if (getChildFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusReceived(BaseEvent baseEvent){
        if (baseEvent != null) {
            receiveEventBusMsg(baseEvent);
        }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) presenter.detachView();
    }

}
