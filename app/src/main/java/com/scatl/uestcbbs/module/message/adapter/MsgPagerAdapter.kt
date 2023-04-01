package com.scatl.uestcbbs.module.message.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.message.view.AtMeMsgFragment
import com.scatl.uestcbbs.module.message.view.DianPingMsgFragment
import com.scatl.uestcbbs.module.message.view.PrivateMsgFragment
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgFragment
import com.scatl.uestcbbs.module.message.view.SystemMsgFragment

/**
 * Created by sca_tl at 2023/3/15 20:07
 */
class MsgPagerAdapter(fragmentActivity: FragmentActivity, ) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.apply {
            add(PrivateMsgFragment.getInstance(null))
            add(ReplyMeMsgFragment.getInstance(null))
            add(AtMeMsgFragment.getInstance(null))
            add(DianPingMsgFragment.getInstance(null))
            add(SystemMsgFragment.getInstance(null))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}