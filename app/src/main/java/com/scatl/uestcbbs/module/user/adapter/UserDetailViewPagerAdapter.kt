package com.scatl.uestcbbs.module.user.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.post.view.CommonPostFragment
import com.scatl.uestcbbs.module.user.view.UserMainPageFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/6/2 9:26
 */
class UserDetailViewPagerAdapter(fragmentActivity: FragmentActivity, uid: Int) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = mutableListOf<Fragment>()

    init {

        fragments.add(UserMainPageFragment.getInstance(Bundle().apply {
            putInt(Constant.IntentKey.USER_ID, uid)
        }))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_USER_POST)
            putInt(Constant.IntentKey.USER_ID, uid)
        }))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_USER_REPLY)
            putInt(Constant.IntentKey.USER_ID, uid)
        }))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_USER_FAVORITE)
            putInt(Constant.IntentKey.USER_ID, uid)
        }))
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}