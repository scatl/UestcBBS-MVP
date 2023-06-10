package com.scatl.uestcbbs.module.board.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.annotation.PostSortByType
import com.scatl.uestcbbs.module.board.view.BoardPostFragment
import com.scatl.uestcbbs.util.Constant

/**
 * created by sca_tl at 2023/6/10 13:34
 */
class BoardViewPagerAdapter(fragmentActivity: FragmentActivity,
                            boardIds: List<Int>) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        boardIds.forEach {
            val bundle = Bundle().apply {
                putString(Constant.IntentKey.TYPE, PostSortByType.TYPE_ALL)
                putInt(Constant.IntentKey.BOARD_ID, it)
                putInt(Constant.IntentKey.FILTER_ID, 0)
            }
            fragments.add(BoardPostFragment.getInstance(bundle))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}