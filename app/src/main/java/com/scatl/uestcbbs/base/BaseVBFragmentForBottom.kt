package com.scatl.uestcbbs.base

import android.os.Bundle
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.FragmentForBottomBinding
import com.scatl.uestcbbs.module.post.view.DianPingFragment
import com.scatl.uestcbbs.module.post.view.PingFenFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/4/12 20:15
 */
class BaseVBFragmentForBottom: BaseVBBottomFragment<BaseVBPresenter<BaseView>, BaseView, FragmentForBottomBinding>(), BaseView {

    private var mBusiness: String? = null

    companion object {
        const val BIZ_DIANPING = "biz_dianping"
        const val BIZ_PINGFEN = "biz_pingfen"

        fun getInstance(bundle: Bundle?) = BaseVBFragmentForBottom().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentForBottomBinding.inflate(layoutInflater)

    override fun getBundle(bundle: Bundle?) {
        mBusiness = bundle?.getString(Constant.IntentKey.TYPE, null)
    }

    override fun initView() {
        val fragment = when(mBusiness) {
            BIZ_DIANPING -> {
                mBinding.title.text = "点评列表"
                DianPingFragment.getInstance(arguments)
            }

            BIZ_PINGFEN -> {
                mBinding.title.text = "评分列表"
                PingFenFragment.getInstance(arguments)
            }

            else -> {
                mBinding.title.text = "无效的类型"
                mBinding.hint.text = "需要传入业务类型"
                null
            }
        }

        if (fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    override fun initPresenter() = BaseVBPresenter<BaseView>()

    override fun setMaxHeightMultiplier() = 0.92
}