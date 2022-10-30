package com.scatl.uestcbbs.module.account.view;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.postview.MyClickableSpan;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.module.account.presenter.LoginPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends BaseDialogFragment implements LoginView {

    private AppCompatEditText userName, userPsw;
    private TextView hint, question, ruleText;
    private Button loginBtn;
    private View questionLayout;
    private CheckBox agreeRule;
    private View answerLayout;

    private LoginPresenter loginPresenter;

    public static final String LOGIN_FOR_SUPER_ACCOUNT = "super";
    public static final String LOGIN_FOR_SIMPLE_ACCOUNT = "simple";

    private int selectedQuestion;

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
        question = view.findViewById(R.id.bottom_fragment_login_question);
        questionLayout = view.findViewById(R.id.bottom_fragment_login_question_layout);
        answerLayout = view.findViewById(R.id.bottom_fragment_login_answer_layout);
        agreeRule = view.findViewById(R.id.bottom_fragment_login_agree_rule_cb);
        ruleText = view.findViewById(R.id.bottom_fragment_login_agree_rule_text);
    }

    @Override
    protected void initView() {

        loginPresenter = (LoginPresenter) presenter;

        loginBtn.setOnClickListener(this);
        questionLayout.setOnClickListener(this);

        answerLayout.setVisibility(View.GONE);

        SpannableString spannableString = new SpannableString("《清水河畔论坛总版规》");
        MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), "https://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=752718");
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ruleText.setText("我已阅读并同意");
        ruleText.setMovementMethod(LinkMovementMethod.getInstance());
        ruleText.append(spannableString);

        ((TextView)view.findViewById(R.id.text13)).setText("添加帐号");
        CommonUtil.showSoftKeyboard(mActivity, userName, 10);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.bottom_fragment_login_login_btn) {
            if (!agreeRule.isChecked()) {
                showToast("请勾选“我已阅读并同意《清水河畔论坛总版规》”", ToastType.TYPE_WARNING);
            } else {
                loginPresenter.login(mActivity, userName.getText().toString(), userPsw.getText().toString());
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

        String [] questions = getResources().getStringArray(R.array.login_question);
        question.setText(questions[position]);
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
        hint.setVisibility(View.VISIBLE);
    }

}
