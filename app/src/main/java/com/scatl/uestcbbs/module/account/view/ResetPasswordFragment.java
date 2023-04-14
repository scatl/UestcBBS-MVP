package com.scatl.uestcbbs.module.account.view;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ResetPswType;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.account.presenter.ResetPasswordPresenter;
import com.scatl.uestcbbs.util.AnimationUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior;

public class ResetPasswordFragment extends BaseBottomFragment implements ResetPasswordView{

    View resetLayout, findLayout, layout;
    TextView changeMode, title, hint;
    Button resetBtn, findBtn;
    LottieAnimationView loading;
    TextInputEditText findStuId, findPortalPsw;
    TextInputEditText resetUserName, resetRealName, resetNewPsw, resetNewPsw2, resetStuId, resetPortalPsw;

    ResetPasswordPresenter resetPasswordPresenter;
    String type, formHash;

    public static ResetPasswordFragment getInstance(Bundle bundle) {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        resetPasswordFragment.setArguments(bundle);
        return resetPasswordFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            type = bundle.getString(Constant.IntentKey.TYPE, "");
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_reset_password;
    }

    @Override
    protected void findView() {
        layout = view.findViewById(R.id.reset_password_layout);
        resetLayout = view.findViewById(R.id.reset_psw_reset_layout);
        findLayout = view.findViewById(R.id.reset_psw_find_layout);
        changeMode = view.findViewById(R.id.reset_psw_change_mode);
        title = view.findViewById(R.id.reset_psw_title);
        resetBtn = view.findViewById(R.id.reset_psw_reset_btn);
        findBtn = view.findViewById(R.id.reset_psw_find_btn);
        findStuId = view.findViewById(R.id.reset_psw_find_stu_id);
        findPortalPsw = view.findViewById(R.id.reset_psw_find_portal_psw);
        loading = view.findViewById(R.id.reset_password_loading);
        hint = view.findViewById(R.id.reset_password_hint);
        resetUserName = view.findViewById(R.id.reset_password_reset_user_name);
        resetRealName = view.findViewById(R.id.reset_password_reset_real_name);
        resetNewPsw = view.findViewById(R.id.reset_password_reset_new_psw);
        resetNewPsw2 = view.findViewById(R.id.reset_password_reset_new_psw2);
        resetStuId = view.findViewById(R.id.reset_password_reset_stu_id);
        resetPortalPsw = view.findViewById(R.id.reset_password_reset_portal_psw);
    }

    @Override
    protected void initView() {
        resetPasswordPresenter = (ResetPasswordPresenter) presenter;

        mBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
        layout.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        resetPasswordPresenter.getFormHash();

        if (ResetPswType.TYPE_RESET.equals(type)) {
            title.setText("重置密码");
            changeMode.setText("找回用户名>");
            resetLayout.setVisibility(View.VISIBLE);
            findLayout.setVisibility(View.GONE);
        } else {
            title.setText("找回用户名");
            changeMode.setText("重置密码>");
            resetLayout.setVisibility(View.GONE);
            findLayout.setVisibility(View.VISIBLE);
        }

        changeMode.setOnClickListener(this);
        findBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ResetPasswordPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.reset_psw_change_mode) {
            if (resetLayout.getVisibility() == View.VISIBLE) {
                findLayout.setAnimation(AnimationUtil.showFromLeft());
                resetLayout.setAnimation(AnimationUtil.hideToRight());
                findLayout.setVisibility(View.VISIBLE);
                resetLayout.setVisibility(View.GONE);
                title.setText("找回用户名");
                changeMode.setText("重置密码>");
            } else if (findLayout.getVisibility() == View.VISIBLE) {
                resetLayout.setAnimation(AnimationUtil.showFromLeft());
                findLayout.setAnimation(AnimationUtil.hideToRight());
                resetLayout.setVisibility(View.VISIBLE);
                findLayout.setVisibility(View.GONE);
                title.setText("重置密码");
                changeMode.setText("找回用户名>");
            }
        }
        if (view.getId() == R.id.reset_psw_find_btn) {
            findBtn.setText("请稍候");
            findBtn.setEnabled(false);
            resetPasswordPresenter.findUserName(formHash, findStuId.getText().toString(), findPortalPsw.getText().toString());
        }

        if (view.getId() == R.id.reset_psw_reset_btn) {
            resetBtn.setText("请稍候");
            resetBtn.setEnabled(false);
            resetPasswordPresenter.resetPassword(formHash, resetUserName.getText().toString(), resetRealName.getText().toString(),
                    resetNewPsw.getText().toString(), resetNewPsw2.getText().toString(), resetStuId.getText().toString(), resetPortalPsw.getText().toString());
        }
    }

    @Override
    public void onFindUserNameSuccess(String msg) {
        findBtn.setText("查询关联帐号");
        findBtn.setEnabled(true);

        final AlertDialog report_dialog = new MaterialAlertDialogBuilder(mActivity)
                .setTitle("查询成功")
                .setMessage(msg)
                .create();
        report_dialog.show();
    }

    @Override
    public void onFindUserNameError(String msg) {
        findBtn.setText("查询关联帐号");
        findBtn.setEnabled(true);
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onResetPswSuccess(String msg) {
        showToast(msg, ToastType.TYPE_SUCCESS);
        dismiss();
    }

    @Override
    public void onResetPswError(String msg) {
        resetBtn.setText("重置密码");
        resetBtn.setEnabled(true);
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetFormHashSuccess(String formHash) {
        this.formHash = formHash;
        loading.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetFormHashError(String msg) {
        loading.setVisibility(View.GONE);
        hint.setText(msg);
    }
}