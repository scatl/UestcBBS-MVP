package com.scatl.uestcbbs.module.post.view

import android.os.Bundle
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.databinding.FragmentCopyContentBinding
import com.scatl.uestcbbs.module.post.presenter.CopyContentPresenter
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl on 2022/12/30 15:15
 */
class CopyContentFragment: BaseVBBottomFragment<CopyContentPresenter, CopyContentView, FragmentCopyContentBinding>(), CopyContentView {

    private var mText = ""

    companion object {
        fun getInstance(bundle: Bundle?) = CopyContentFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mText = bundle.getString(Constant.IntentKey.CONTENT, "")
        }
    }

    override fun getViewBinding() = FragmentCopyContentBinding.inflate(layoutInflater)

    override fun initView() {
        mBinding.text.text = mText
    }

    override fun initPresenter() = CopyContentPresenter()

    override fun setMaxHeightMultiplier() = 0.92
}