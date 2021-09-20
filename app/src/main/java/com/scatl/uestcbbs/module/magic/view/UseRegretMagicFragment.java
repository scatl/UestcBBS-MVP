package com.scatl.uestcbbs.module.magic.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UseRegretMagicBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.magic.presenter.UseMagicPresenter;
import com.scatl.uestcbbs.module.magic.presenter.UseRegretMagicPresenter;
import com.scatl.uestcbbs.util.Constant;

import org.greenrobot.eventbus.EventBus;


public class UseRegretMagicFragment extends BaseBottomFragment implements UseRegretMagicView{

    private ImageView icon;
    private TextView name, dsp, otherInfo, useSuccessText, hint;
    private ProgressBar progressBar;
    private View contentLayout;
    private Button useBtn;
    private View useSuccessView;
    private UseRegretMagicPresenter useRegretMagicPresenter;

    int tid, pid;
    String formhash;

    public static UseRegretMagicFragment getInstance(Bundle bundle) {
        UseRegretMagicFragment useRegretMagicFragment = new UseRegretMagicFragment();
        useRegretMagicFragment.setArguments(bundle);
        return useRegretMagicFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_use_magic;
    }

    @Override
    protected void findView() {
        icon = view.findViewById(R.id.use_magic_fragment_icon);
        name = view.findViewById(R.id.use_magic_fragment_name);
        dsp = view.findViewById(R.id.use_magic_fragment_dsp);
        otherInfo = view.findViewById(R.id.use_magic_fragment_other_info);
        progressBar = view.findViewById(R.id.use_magic_fragment_progressbar);
        contentLayout = view.findViewById(R.id.use_magic_content_layout);
        useBtn = view.findViewById(R.id.use_magic_fragment_use_btn);
        useSuccessView = view.findViewById(R.id.use_magic_fragment_use_success_view);
        useSuccessText = view.findViewById(R.id.use_magic_fragment_use_success_text);
        hint = view.findViewById(R.id.use_magic_fragment_hint);
    }

    @Override
    protected void initView() {
        useRegretMagicPresenter = (UseRegretMagicPresenter) presenter;

        contentLayout.setVisibility(View.GONE);
        useBtn.setOnClickListener(this::onClickListener);
        useRegretMagicPresenter.getUseRegretMagicDetail(pid + ":" + tid);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UseRegretMagicPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.use_magic_fragment_use_btn) {
            useBtn.setText("使用中，请稍候...");
            useBtn.setEnabled(false);

            useRegretMagicPresenter.confirmUseRegretMagic(formhash, pid, tid);
        }
    }

    @Override
    public void onGetMagicDetailSuccess(UseRegretMagicBean useRegretMagicBean, String formhash) {
        this.formhash = formhash;
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        hint.setText("");
        GlideLoader4Common.simpleLoad(mActivity, useRegretMagicBean.icon, icon);
        name.setText(useRegretMagicBean.name);
        otherInfo.setText(useRegretMagicBean.otherInfo);
    }

    @Override
    public void onGetMagicDetailError(String msg) {
        hint.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUseMagicSuccess(String msg) {
        useSuccessView.setVisibility(View.VISIBLE);
        useSuccessText.setText(msg);
        contentLayout.setVisibility(View.GONE);
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.USE_MAGIC_SUCCESS));
    }

    @Override
    public void onUseMagicError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
        useBtn.setText("使用");
        useBtn.setEnabled(true);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }

}