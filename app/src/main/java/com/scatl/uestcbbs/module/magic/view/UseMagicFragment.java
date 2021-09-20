package com.scatl.uestcbbs.module.magic.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UseMagicBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.magic.presenter.UseMagicPresenter;
import com.scatl.uestcbbs.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;


public class UseMagicFragment extends BaseBottomFragment implements UseMagicView{

    private ImageView icon;
    private TextView name, dsp, otherInfo, useSuccessText, hint;
    private ProgressBar progressBar;
    private View contentLayout;
    private Button useBtn;
    private View useSuccessView;
    private UseMagicPresenter useMagicPresenter;

    String magicId, formhash;

    public static UseMagicFragment getInstance(Bundle bundle) {
        UseMagicFragment useMagicFragment = new UseMagicFragment();
        useMagicFragment.setArguments(bundle);
        return useMagicFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            magicId = bundle.getString(Constant.IntentKey.MAGIC_ID);
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
        useMagicPresenter = (UseMagicPresenter) presenter;
        contentLayout.setVisibility(View.GONE);
        useBtn.setOnClickListener(this::onClickListener);
        useMagicPresenter.getUseMagicDetail(magicId);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UseMagicPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.use_magic_fragment_use_btn) {
            useBtn.setText("使用中，请稍候...");
            useBtn.setEnabled(false);

            useMagicPresenter.confirmUseMagic(formhash, magicId);
        }
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }

    @Override
    public void onGetUseMagicDetailSuccess(UseMagicBean useMagicBean, String formhash) {
        this.formhash = formhash;
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        hint.setText("");
        GlideLoader4Common.simpleLoad(mActivity, useMagicBean.icon, icon);
        name.setText(useMagicBean.name);
        dsp.setText(useMagicBean.dsp);
        otherInfo.setText(useMagicBean.otherInfo);
    }

    @Override
    public void onGetUseMagicDetailError(String msg) {
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
}