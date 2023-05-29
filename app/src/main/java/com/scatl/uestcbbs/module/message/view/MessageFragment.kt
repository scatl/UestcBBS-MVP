package com.scatl.uestcbbs.module.message.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IMessageRefresh
import com.scatl.uestcbbs.databinding.FragmentMessageBinding
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.message.adapter.MsgPagerAdapter
import com.scatl.uestcbbs.module.message.presenter.MessagePresenter
import com.scatl.util.ColorUtil
import com.scatl.util.desensitize

/**
 * Created by sca_tl at 2023/3/15 19:40
 */
class MessageFragment: BaseVBFragment<MessagePresenter, MessageView, FragmentMessageBinding>(), MessageView {

    companion object {
        fun getInstance(bundle: Bundle?) = MessageFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentMessageBinding.inflate(layoutInflater)

    override fun initPresenter() = MessagePresenter()

    override fun initView() {
        super.initView()
        val titles = arrayOf("私信", "回复", "提到", "点评", "系统")
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = MsgPagerAdapter(context as FragmentActivity)
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
        when(baseEvent.eventCode) {
            BaseEvent.EventCode.SET_MSG_COUNT -> {
                setBadge()
            }
            BaseEvent.EventCode.LOGIN_SUCCESS -> {
                parentFragmentManager.fragments.forEach {
                    (it as? IMessageRefresh)?.onRefresh()
                }
            }
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