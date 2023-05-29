package com.scatl.uestcbbs.module.credit.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.databinding.FragmentWaterTaskBinding
import com.scatl.uestcbbs.module.credit.adapter.WaterTaskPagerAdapter
import com.scatl.uestcbbs.module.credit.presenter.WaterTaskPresenter
import com.scatl.util.ColorUtil
import com.scatl.util.desensitize

/**
 * Created by sca_tl at 2023/4/6 20:04
 */
class WaterTaskFragment: BaseVBBottomFragment<WaterTaskPresenter, WaterTaskView, FragmentWaterTaskBinding>(), WaterTaskView {

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentWaterTaskBinding.inflate(layoutInflater)

    override fun initView() {
        val titles = arrayOf("进行中", "新任务", "已完成", "已失败")
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = WaterTaskPagerAdapter(context as FragmentActivity)
            desensitize()
        }
        mBinding.tabLayout.setSelectedTabIndicatorColor(ColorUtil.getAlphaColor(0.65f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)))
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun initPresenter() = WaterTaskPresenter()

}