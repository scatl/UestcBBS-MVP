package com.scatl.uestcbbs.module.post.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.uestcbbs.module.post.view.postdetail2.P2CommentFragment
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DaShangFragment
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DianPingFragment
import com.scatl.uestcbbs.module.post.view.postdetail2.P2DianZanFragment
import com.scatl.uestcbbs.util.Constant

/**
 * Created by sca_tl on 2022/12/5 16:30
 */
class NewPostDetailPagerAdapter(fragmentActivity: FragmentActivity,
                                tid: Int,
                                pid: Int,
                                uid: Int,
                                boardId: Int) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.let {
            it.add(
                P2DianZanFragment.getInstance(Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, tid)
                })
            )

            it.add(
                P2CommentFragment.getInstance(Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, tid)
                    putInt(Constant.IntentKey.USER_ID, uid)
                    putInt(Constant.IntentKey.BOARD_ID, boardId)
                })
            )

            it.add(
                P2DianPingFragment.getInstance(Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, tid)
                    putInt(Constant.IntentKey.POST_ID, pid)
                })
            )

            it.add(
                P2DaShangFragment.getInstance(Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, tid)
                    putInt(Constant.IntentKey.POST_ID, pid)
                })
            )
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}