package com.scatl.uestcbbs.module.message.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.message.view.AtMeMsgFragment
import com.scatl.uestcbbs.module.message.view.DianPingMsgFragment
import com.scatl.uestcbbs.module.message.view.PrivateMsgFragment
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgFragment
import com.scatl.uestcbbs.module.message.view.SystemMsgFragment
import com.scatl.uestcbbs.module.post.view.CommentFragment
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DaShangFragment
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DianPingFragment

/**
 * Created by sca_tl at 2023/3/15 20:07
 */
class NewMsgPagerAdapter(fragmentActivity: FragmentActivity, ) : FragmentStateAdapter(fragmentActivity) {

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