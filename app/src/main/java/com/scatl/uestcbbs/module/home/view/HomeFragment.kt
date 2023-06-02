package com.scatl.uestcbbs.module.home.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IHomeRefresh
import com.scatl.uestcbbs.callback.TabListenerAdapter
import com.scatl.uestcbbs.databinding.FragmentHomeBinding
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.account.view.AccountManageActivity
import com.scatl.uestcbbs.module.home.adapter.HomeViewPagerAdapter
import com.scatl.uestcbbs.module.home.presenter.HomePresenter
import com.scatl.uestcbbs.module.search.view.SearchActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.load
import com.scatl.util.desensitize
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/6/1 15:38
 */
class HomeFragment: BaseVBFragment<HomePresenter, HomeView, FragmentHomeBinding>(), HomeView, AppBarLayout.OnOffsetChangedListener {

    private var before = 0
    private var shortCutHot = false

    companion object {
        fun getInstance(bundle: Bundle?) = HomeFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun initPresenter() = HomePresenter()

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            shortCutHot = it.getBoolean(Constant.IntentKey.SHORT_CUT_HOT, false)
        }
    }

    override fun initView() {
        super.initView()
        mBinding.userAvatar.setOnClickListener(this)
        mBinding.searchLayout.setOnClickListener(this)
        mBinding.appBar.addOnOffsetChangedListener(this)

        mBinding.viewpager.apply {
            offscreenPageLimit = 5
            desensitize()
            adapter = HomeViewPagerAdapter(this@HomeFragment)
            currentItem = 0
        }

        mBinding.tabLayout.addOnTabSelectedListener(object : TabListenerAdapter() {
            override fun onTabReselected(tab: TabLayout.Tab) {
                EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.HOME_REFRESH))
            }
        })

        val titles = arrayOf("最新发表", "最新回复", "热门", "精华", "淘专辑")
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.setText(titles[position])
        }.attach()

        if (shortCutHot) {
            mBinding.viewpager.currentItem = 2
            shortCutHot = false
        }

        if (SharePrefUtil.isLogin(context)) {
            mBinding.userAvatar.load(SharePrefUtil.getAvatar(context))
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            mBinding.userAvatar -> {
                if (SharePrefUtil.isLogin(context)) {
                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra(Constant.IntentKey.USER_ID, SharePrefUtil.getUid(context))
                    startActivity(intent)
                } else {
                    startActivity(Intent(context, AccountManageActivity::class.java))
                }
            }
            mBinding.searchLayout -> {
                startActivity(Intent(context, SearchActivity::class.java))
            }
        }
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        when(baseEvent.eventCode) {
            BaseEvent.EventCode.LOGIN_SUCCESS -> {
                mBinding.userAvatar.load(SharePrefUtil.getAvatar(context))
            }
            BaseEvent.EventCode.LOGOUT_SUCCESS -> {
                mBinding.userAvatar.load(R.drawable.ic_default_avatar)
            }
            BaseEvent.EventCode.HOME_REFRESH -> {
                val fragment = childFragmentManager.findFragmentByTag(
                    "f" + mBinding.viewpager.adapter?.getItemId(mBinding.viewpager.currentItem)
                )
                (fragment as? IHomeRefresh?)?.onRefresh()
            }
            BaseEvent.EventCode.SET_MSG_COUNT -> {
                val badgeDrawable = mBinding.tabLayout.getTabAt(4)?.getOrCreateBadge()
                if (MessageManager.INSTANCE.collectionUnreadCount != 0) {
                    badgeDrawable?.isVisible = true
                    badgeDrawable?.number = MessageManager.INSTANCE.collectionUnreadCount
                } else {
                    badgeDrawable?.isVisible = false
                    badgeDrawable?.clearNumber()
                }
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val after = Math.abs(verticalOffset)
        when {
            after == 0 -> {

            }
            after == appBarLayout.totalScrollRange || after - before > 0 -> {
                EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, true))
            }
            else -> {
                EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, false))
            }
        }
        before = after
    }
}