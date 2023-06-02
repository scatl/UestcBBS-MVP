package com.scatl.uestcbbs.module.account.view

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBDialogFragment
import com.scatl.uestcbbs.databinding.FragmentLoginBinding
import com.scatl.uestcbbs.entity.LoginBean
import com.scatl.uestcbbs.module.account.presenter.LoginPresenter
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.uestcbbs.widget.span.CustomClickableSpan
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/6/2 14:10
 */
class LoginFragment: BaseVBDialogFragment<LoginPresenter, LoginView, FragmentLoginBinding>(), LoginView {

    companion object {
        fun getInstance(bundle: Bundle?) = LoginFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override fun initPresenter() = LoginPresenter()

    override fun initView() {
        mBinding.loginBtn.setOnClickListener(this)

        val spannableString = SpannableString("《清水河畔论坛总版规》")
        val clickableSpan = CustomClickableSpan(context, "https://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=752718", false)
        spannableString.setSpan(clickableSpan, 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        mBinding.ruleText.text = "我已阅读并同意"
        mBinding.ruleText.movementMethod = LinkMovementMethod.getInstance()
        mBinding.ruleText.append(spannableString)

        CommonUtil.showSoftKeyboard(context, mBinding.userName, 100)
    }

    override fun onClick(v: View) {
        if (v == mBinding.loginBtn) {
            if (mBinding.ruleCheckbox.isChecked) {
                mBinding.loginBtn.isEnabled = false
                mPresenter?.login(context, mBinding.userName.text.toString(), mBinding.userPsw.text.toString())
            } else {
                showToast("请勾选“我已阅读并同意《清水河畔论坛总版规》”", ToastType.TYPE_WARNING)
            }
        }
    }

    override fun onLoginSuccess(loginBean: LoginBean) {
        CommonUtil.hideSoftKeyboard(context, mBinding.userName)
        EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS, loginBean))
        dismiss()
    }

    override fun onLoginError(msg: String?) {
        mBinding.loginBtn.isEnabled = true
        showToast(msg, ToastType.TYPE_WARNING)
    }

}