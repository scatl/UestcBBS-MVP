package com.scatl.uestcbbs.module.message.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentNewMsgBinding
import com.scatl.uestcbbs.module.message.MessageManager
import com.scatl.uestcbbs.module.message.adapter.NewMsgPagerAdapter
import com.scatl.uestcbbs.module.message.presenter.NewMsgPresenter
import com.scatl.uestcbbs.util.ColorUtil
import com.scatl.uestcbbs.util.desensitize

/**
 * Created by sca_tl at 2023/3/15 19:40
 */
class NewMessageFragment: BaseVBFragment<NewMsgPresenter, NewMsgView, FragmentNewMsgBinding>(), NewMsgView {

    companion object {
        fun getInstance(bundle: Bundle?) = NewMessageFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentNewMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = NewMsgPresenter()

    override fun initView() {
        super.initView()
        val titles = arrayOf("私信", "回复", "AT", "点评", "系统")
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = NewMsgPagerAdapter(context as FragmentActivity)
            desensitize()
        }
        mBinding.tabLayout.setSelectedTabIndicatorColor(ColorUtil.getAlphaColor(0.65f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)))
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        setBadge()
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
            setBadge()
        }
    }

    private fun setBadge() {
        mBinding.tabLayout.getTabAt(0)?.orCreateBadge?.apply {
            horizontalOffset = -5
            number = MessageManager.INSTANCE.pmUnreadCount
            isVisible = MessageManager.INSTANCE.pmUnreadCount != 0
        }

        mBinding.tabLayout.getTabAt(1)?.orCreateBadge?.apply {
            horizontalOffset = -5
            number = MessageManager.INSTANCE.replyUnreadCount
            isVisible = MessageManager.INSTANCE.replyUnreadCount != 0
        }

        mBinding.tabLayout.getTabAt(2)?.orCreateBadge?.apply {
            horizontalOffset = -5
            number = MessageManager.INSTANCE.atUnreadCount
            isVisible = MessageManager.INSTANCE.atUnreadCount != 0
        }

        mBinding.tabLayout.getTabAt(3)?.orCreateBadge?.apply {
            horizontalOffset = -5
            number = MessageManager.INSTANCE.dianPingUnreadCount
            isVisible = MessageManager.INSTANCE.dianPingUnreadCount != 0
        }

        mBinding.tabLayout.getTabAt(4)?.orCreateBadge?.apply {
            horizontalOffset = -5
            number = MessageManager.INSTANCE.systemUnreadCount
            isVisible = MessageManager.INSTANCE.systemUnreadCount != 0
        }
    }
}