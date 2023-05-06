package com.scatl.uestcbbs.module.collection.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.collection.view.CollectionListFragment
import com.scatl.uestcbbs.module.message.view.AtMeMsgFragment
import com.scatl.uestcbbs.module.message.view.DianPingMsgFragment
import com.scatl.uestcbbs.module.message.view.PrivateMsgFragment
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgFragment
import com.scatl.uestcbbs.module.message.view.SystemMsgFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/3/15 20:07
 */
class CollectionPagerAdapter(fragmentActivity: FragmentActivity, ) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.apply {
            add(CollectionListFragment.getInstance(Bundle().apply {
                putString(Constant.IntentKey.TYPE, CollectionListFragment.TYPE_MINE)
            }))

            add(CollectionListFragment.getInstance(Bundle().apply {
                putString(Constant.IntentKey.TYPE, CollectionListFragment.TYPE_RECOMMEND)
            }))

            add(CollectionListFragment.getInstance(Bundle().apply {
                putString(Constant.IntentKey.TYPE, CollectionListFragment.TYPE_ALL)
            }))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}