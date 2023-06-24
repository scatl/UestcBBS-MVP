package com.scatl.uestcbbs.module.credit.view

import android.os.Bundle
import android.view.View
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentCreditTransferBinding
import com.scatl.uestcbbs.module.credit.presenter.CreditTransferPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast

/**
 * Created by sca_tl at 2023/6/20 14:11
 */
class CreditTransferFragment: BaseVBFragment<CreditTransferPresenter, CreditTransferView, FragmentCreditTransferBinding>(), CreditTransferView {

    private var happyBoyName: String? = null

    override fun getViewBinding() = FragmentCreditTransferBinding.inflate(layoutInflater)

    override fun initPresenter() = CreditTransferPresenter()

    companion object {
        fun getInstance(bundle: Bundle?) = CreditTransferFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            happyBoyName = bundle.getString(Constant.IntentKey.USER_NAME)
        }
    }

    override fun initView() {
        bindClickEvent(mBinding.confirmBtn)
        happyBoyName?.let { mBinding.happyBoyEt.setText(it) }
        mBinding.statusView.loading(mBinding.creditTransferLayout)
        mPresenter?.getCreditFormHash()
    }

    override fun onClick(v: View) {
        when(v) {
            mBinding.confirmBtn -> {
                if (mBinding.happyBoyEt.text.toString().isEmpty()) {
                    showToast("请填写转账目标用户名", ToastType.TYPE_WARNING)
                } else if (mBinding.shuidiCountEt.text.toString().isEmpty()) {
                    showToast("请填写转账水滴数量", ToastType.TYPE_WARNING)
                } else if (mBinding.passwordEt.text.toString().isEmpty()) {
                    showToast("请填写您的登录密码", ToastType.TYPE_WARNING)
                } else {
                    mBinding.confirmBtn.isEnabled = false
                    mBinding.confirmBtn.text = "请稍候..."
                    mPresenter?.creditTransfer(
                        SharePrefUtil.getForumHash(context),
                        mBinding.shuidiCountEt.text.toString(),
                        mBinding.happyBoyEt.text.toString(),
                        mBinding.passwordEt.text.toString(),
                        mBinding.messageEt.text.toString()
                    )
                }
            }
        }
    }

    override fun onGetFormHashSuccess(formHash: String?) {
        mBinding.statusView.success()
    }

    override fun onGetFormHashError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    override fun onTransferSuccess(msg: String?) {
        showToast(msg, ToastType.TYPE_SUCCESS)
        (parentFragment as? BaseVBBottomFragment<*,*,*>?)?.dismiss()
    }

    override fun onTransferError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
        mBinding.confirmBtn.isEnabled = true
        mBinding.confirmBtn.text = "确认转账"
    }
}