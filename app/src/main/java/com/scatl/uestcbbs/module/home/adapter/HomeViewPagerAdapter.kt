package com.scatl.uestcbbs.module.home.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.collection.view.CollectionFragment
import com.scatl.uestcbbs.module.home.view.LatestPostFragment
import com.scatl.uestcbbs.module.post.view.CommonPostFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl at 2023/6/1 15:30
 */
class HomeViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.add(LatestPostFragment.getInstance(null))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_NEW_REPLY_POST)
        }))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_HOT_POST)
        }))

        fragments.add(CommonPostFragment.getInstance(Bundle().apply {
            putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_ESSENCE_POST)
        }))

        fragments.add(CollectionFragment.getInstance(null))

    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}