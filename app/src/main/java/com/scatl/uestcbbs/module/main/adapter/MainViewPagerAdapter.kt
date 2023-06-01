package com.scatl.uestcbbs.module.main.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.board.view.BoardListFragment
import com.scatl.uestcbbs.module.home.view.HomeFragment
import com.scatl.uestcbbs.module.message.view.MessageFragment
import com.scatl.uestcbbs.module.mine.view.MineFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/4/11 17:16
 */
class MainViewPagerAdapter(fragmentActivity: FragmentActivity, shortCutHotPost: Boolean) : FragmentStateAdapter(fragmentActivity) {
    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.add(HomeFragment.getInstance(Bundle().apply {
            putBoolean(Constant.IntentKey.SHORT_CUT_HOT, shortCutHotPost)
        }))
        fragments.add(BoardListFragment.getInstance(null))
        fragments.add(MessageFragment.getInstance(null))
        fragments.add(MineFragment.getInstance(null))
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}