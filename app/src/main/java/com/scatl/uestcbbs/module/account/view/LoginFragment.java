package com.scatl.uestcbbs.module.account.view;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.module.account.presenter.LoginPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends BaseDialogFragment implements LoginView{

    private AppCompatEditText userName, userPsw;
    private TextView hint, dsp;
    private Button loginBtn, registerBtn;

    private LoginPresenter loginPresenter;

    public static final String LOGIN_FOR_SUPER_ACCOUNT = "super";
    public static final String LOGIN_FOR_SIMPLE_ACCOUNT = "simple";

    private String loginType;
    private String userNameForSuperLogin;

    public static LoginFragment getInstance(Bundle bundle) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(bundle);
        return loginFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            loginType = bundle.getString(Constant.IntentKey.LOGIN_TYPE, LOGIN_FOR_SIMPLE_ACCOUNT);
            userNameForSuperLogin = bundle.getString(Constant.IntentKey.USER_NAME, null);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_bottom_login;
    }

    @Override
    protected void findView() {
        userName = view.findViewById(R.id.bottom_fragment_login_user_name);
        userPsw = view.findViewById(R.id.bottom_fragment_login_user_psw);
        hint = view.findViewById(R.id.bottom_fragment_login_hint);
        loginBtn = view.findViewById(R.id.bottom_fragment_login_login_btn);
        registerBtn = view.findViewById(R.id.bottom_fragment_login_register_btn);
        dsp = view.findViewById(R.id.bottom_fragment_login_dsp);
    }

    @Override
    protected void initView() {

        loginPresenter = (LoginPresenter) presenter;

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        if (LOGIN_FOR_SUPER_ACCOUNT.equals(loginType) && userNameForSuperLogin != null) {
            ((TextView)view.findViewById(R.id.text13)).setText("高级授权");
            dsp.setVisibility(View.VISIBLE);
            dsp.setText("温馨提示：高级授权会先获取Cookies，然后利用此Cookies获取上传附件所需的hash参数值");

            view.findViewById(R.id.bottom_fragment_login_register_layout).setVisibility(View.GONE);
            loginBtn.setText("立即授权");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            loginBtn.setLayoutParams(layoutParams);
            userName.setText(userNameForSuperLogin);
            userName.setEnabled(false);
            CommonUtil.showSoftKeyboard(mActivity, userPsw, 10);
        } else {
            CommonUtil.showSoftKeyboard(mActivity, userName, 10);
        }

    }

    @Override
    protected BasePresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.bottom_fragment_login_login_btn) {
            if (LOGIN_FOR_SIMPLE_ACCOUNT.equals(loginType)) {
                loginPresenter.simpleLogin(userName.getText().toString(), userPsw.getText().toString());
            } else if (LOGIN_FOR_SUPER_ACCOUNT.equals(loginType)) {
                loginBtn.setText("获取cookies中，请稍候...");
                loginBtn.setEnabled(false);
                loginPresenter.getCookies(mActivity, userName.getText().toString(), userPsw.getText().toString());
            }
        }

        if (view.getId() == R.id.bottom_fragment_login_register_btn) {
            CommonUtil.openBrowser(mActivity, ApiConstant.User.REGISTER_URL);
        }
    }

    @Override
    public void onSimpleLoginSuccess(LoginBean loginBean) {
        CommonUtil.hideSoftKeyboard(mActivity, userName);
        dismiss();

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS, loginBean));
    }

    @Override
    public void onSimpleLoginError(String msg) {
        hint.setText(msg);
    }

    @Override
    public void onGetCookiesSuccess(String msg) {
        loginBtn.setText(msg);
        loginPresenter.getUploadHash(1802999);
    }

    @Override
    public void onGetCookiesError(String msg) {
        hint.setText(msg);
        loginBtn.setText("立即授权");
        loginBtn.setEnabled(true);
    }

    @Override
    public void onGetUploadHashSuccess(String hash, String msg) {
        SharePrefUtil.setUploadHash(mActivity, hash, userName.getText().toString());
        CommonUtil.hideSoftKeyboard(mActivity, userName);
        dismiss();
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SUPER_LOGIN_SUCCESS));
    }

    @Override
    public void onGetUploadHashError(String msg) {
        hint.setText(msg);
        loginBtn.setText("立即授权");
        loginBtn.setEnabled(true);
    }
}
