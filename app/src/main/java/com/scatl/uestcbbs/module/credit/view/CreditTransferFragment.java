package com.scatl.uestcbbs.module.credit.view;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.credit.presenter.CreditTransferPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior;

public class CreditTransferFragment extends BaseBottomFragment implements CreditTransferView{

    TextInputEditText toUserNameEt, shuiDiAmountEt, passwordEt, messageEt;
    Button confirmBtn;
    View creditTransferLayout;
    String formHash, toUSerName;
    LottieAnimationView loading;
    TextView hint;

    CreditTransferPresenter creditTransferPresenter;

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            toUSerName = bundle.getString(Constant.IntentKey.USER_NAME);
        }
    }

    public static CreditTransferFragment getInstance(Bundle bundle) {
        CreditTransferFragment creditTransferFragment = new CreditTransferFragment();
        creditTransferFragment.setArguments(bundle);
        return creditTransferFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_credit_transfer;
    }

    @Override
    protected void findView() {
        creditTransferLayout = view.findViewById(R.id.credit_transfer_layout);
        toUserNameEt = view.findViewById(R.id.credit_transfer_to_user);
        shuiDiAmountEt = view.findViewById(R.id.credit_transfer_shuidi_amount);
        passwordEt = view.findViewById(R.id.credit_transfer_password);
        confirmBtn = view.findViewById(R.id.credit_transfer_confirm_btn);
        messageEt = view.findViewById(R.id.credit_transfer_message);
        loading = view.findViewById(R.id.credit_transfer_loading);
        hint = view.findViewById(R.id.credit_transfer_hint);
    }

    @Override
    protected void initView() {
        mBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
        creditTransferPresenter = (CreditTransferPresenter) presenter;
        confirmBtn.setOnClickListener(this);
        creditTransferLayout.setVisibility(View.GONE);
        if (toUSerName != null) {
            toUserNameEt.setText(toUSerName);
        }

        creditTransferPresenter.getCreditFormHash();
    }

    @Override
    protected BasePresenter initPresenter() {
        return new CreditTransferPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.credit_transfer_confirm_btn) {
            if (toUserNameEt.getText().toString() == null || toUserNameEt.getText().toString().length() == 0) {
                showToast("请填写转账目标用户名", ToastType.TYPE_WARNING);
            } else if (shuiDiAmountEt.getText().toString() == null || shuiDiAmountEt.getText().toString().length() == 0){
                showToast("请填写转账水滴数量", ToastType.TYPE_WARNING);
            } else if (passwordEt.getText().toString() == null || passwordEt.getText().toString().length() == 0) {
                showToast("请填写您的登录密码", ToastType.TYPE_WARNING);
            } else {
                confirmBtn.setEnabled(false);
                confirmBtn.setText("请稍候...");
                creditTransferPresenter.creditTransfer(formHash,
                        shuiDiAmountEt.getText().toString(),
                        toUserNameEt.getText().toString(),
                        passwordEt.getText().toString(),
                        messageEt.getText().toString());
            }
        }
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }

    @Override
    public void onGetFormHashSuccess(String formHash) {
        this.formHash = formHash;
        hint.setText("");
        loading.setVisibility(View.GONE);
        creditTransferLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetFormHashError(String msg) {
        hint.setText(msg);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onTransferSuccess(String msg) {
        showToast(msg, ToastType.TYPE_SUCCESS);
        dismiss();
    }

    @Override
    public void onTransferError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
        confirmBtn.setEnabled(true);
        confirmBtn.setText("确认转账");
    }
}