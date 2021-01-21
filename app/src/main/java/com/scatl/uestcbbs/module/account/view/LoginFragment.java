package com.scatl.uestcbbs.module.account.view;


import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.postview.MyClickableSpan;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.module.account.presenter.LoginPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends BaseDialogFragment implements LoginView, CompoundButton.OnCheckedChangeListener{

    private AppCompatEditText userName, userPsw, answer;
    private TextView hint, question, ruleText;
    private Button loginBtn;
    private View questionLayout;
    private CheckBox agreeRule, superLoginCb;
    private View answerLayout;

    private LoginPresenter loginPresenter;

    public static final String LOGIN_FOR_SUPER_ACCOUNT = "super";
    public static final String LOGIN_FOR_SIMPLE_ACCOUNT = "simple";

    private String loginType;
    private String userNameForSuperLogin;
    private int selectedQuestion;

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
        question = view.findViewById(R.id.bottom_fragment_login_question);
        questionLayout = view.findViewById(R.id.bottom_fragment_login_question_layout);
        answer = view.findViewById(R.id.bottom_fragment_login_answer);
        answerLayout = view.findViewById(R.id.bottom_fragment_login_answer_layout);
        agreeRule = view.findViewById(R.id.bottom_fragment_login_agree_rule_cb);
        ruleText = view.findViewById(R.id.bottom_fragment_login_agree_rule_text);
        superLoginCb = view.findViewById(R.id.bottom_fragment_login_super_login_cb);
    }

    @Override
    protected void initView() {

        loginPresenter = (LoginPresenter) presenter;

        loginBtn.setOnClickListener(this);
        questionLayout.setOnClickListener(this);

        superLoginCb.setOnCheckedChangeListener(this);

        answerLayout.setVisibility(View.GONE);

        SpannableString spannableString = new SpannableString("《清水河畔论坛总版规》");
        MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), "https://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=752718");
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ruleText.setText("我已阅读并同意");
        ruleText.setMovementMethod(LinkMovementMethod.getInstance());
        ruleText.append(spannableString);

        if (LOGIN_FOR_SUPER_ACCOUNT.equals(loginType) && userNameForSuperLogin != null) {
            ((TextView)view.findViewById(R.id.text13)).setText("高级授权");
            superLoginCb.setVisibility(View.GONE);
            loginBtn.setText("立即授权");
            userName.setText(userNameForSuperLogin);
            userName.setEnabled(false);
            CommonUtil.showSoftKeyboard(mActivity, userPsw, 10);
        } else {
            ((TextView)view.findViewById(R.id.text13)).setText("添加帐号");
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
            if (!agreeRule.isChecked()) {
                showToast("请勾选“我已阅读并同意《清水河畔论坛总版规》”");
            } else {
                superLoginCb.setEnabled(false);
                if (LOGIN_FOR_SIMPLE_ACCOUNT.equals(loginType)) {
                    loginPresenter.simpleLogin(userName.getText().toString(), userPsw.getText().toString());
                } else if (LOGIN_FOR_SUPER_ACCOUNT.equals(loginType)) {
                    loginBtn.setText("获取cookies中，请稍候...");
                    loginBtn.setEnabled(false);
                    loginPresenter.getCookies(mActivity, userName.getText().toString(), userPsw.getText().toString(), selectedQuestion, answer.getText().toString());
                }
            }
        }

        if (view.getId() == R.id.bottom_fragment_login_question_layout) {
            loginPresenter.showLoginReasonDialog(mActivity, selectedQuestion);
        }
    }

    @Override
    public void onLoginReasonSelected(int position) {
        selectedQuestion = position;
        answerLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

        String [ ] questions = getResources().getStringArray(R.array.login_question);
        question.setText(questions[position]);
    }

    @Override
    public void onSimpleLoginSuccess(LoginBean loginBean) {
        if (!superLoginCb.isChecked()) {//未勾选高级授权
            CommonUtil.hideSoftKeyboard(mActivity, userName);
            dismiss();
        } else {//勾选高级授权
            loginBtn.setText("获取cookies中，请稍候...");
            loginBtn.setEnabled(false);
            loginPresenter.getCookies(mActivity, userName.getText().toString(), userPsw.getText().toString(), selectedQuestion, answer.getText().toString());
        }

        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS, loginBean));
    }

    @Override
    public void onSimpleLoginError(String msg) {
        hint.setText(msg);
        hint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetCookiesSuccess(String msg) {
        CommonUtil.hideSoftKeyboard(mActivity, userName);
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SUPER_LOGIN_SUCCESS));
        dismiss();
    }

    @Override
    public void onGetCookiesError(String msg) {
        hint.setText(msg);
        hint.setVisibility(View.VISIBLE);
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
        hint.setVisibility(View.VISIBLE);
        loginBtn.setText("立即授权");
        loginBtn.setEnabled(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        questionLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }
}
