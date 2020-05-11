package com.scatl.uestcbbs.module.account.view;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends BaseDialogFragment implements LoginView{

    private AppCompatEditText userName, userPsw;
    private TextView hint;
    private Button loginBtn, registerBtn;

    private LoginPresenter loginPresenter;

    public static LoginFragment getInstance(Bundle bundle) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(bundle);
        return loginFragment;
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
    }

    @Override
    protected void initView() {

        loginPresenter = (LoginPresenter) presenter;

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        CommonUtil.showSoftKeyboard(mActivity, userName, 10);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.bottom_fragment_login_login_btn) {
            loginPresenter.login(userName.getText().toString(), userPsw.getText().toString());
        }

        if (view.getId() == R.id.bottom_fragment_login_register_btn) {
            CommonUtil.openBrowser(mActivity, ApiConstant.User.REGISTER_URL);
        }
    }

    @Override
    public void onLoginSuccess(LoginBean loginBean) {
        CommonUtil.hideSoftKeyboard(mActivity, userName);
        dismiss();

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS, loginBean));
    }

    @Override
    public void onLoginError(String msg) {
        hint.setText(msg);
    }
}
