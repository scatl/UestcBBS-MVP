package com.scatl.uestcbbs.module.credit.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.module.credit.view.WaterTaskDoingFragment
import com.scatl.uestcbbs.module.credit.view.WaterTaskDoneFragment
import com.scatl.uestcbbs.module.credit.view.WaterTaskFailedFragment
import com.scatl.uestcbbs.module.credit.view.WaterTaskNewFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/3/15 20:07
 */
class WaterTaskPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.apply {
            add(WaterTaskDoingFragment.getInstance(null))
            add(WaterTaskNewFragment.getInstance(null))
            add(WaterTaskDoneFragment.getInstance(null))
            add(WaterTaskFailedFragment.getInstance(null))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}