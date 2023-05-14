package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IMessageRefresh
import com.scatl.uestcbbs.databinding.FragmentSystemMsgBinding
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.message.adapter.SystemMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.SystemMsgPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.module.webview.view.WebViewActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/20 10:26
 */
class SystemMsgFragment: BaseVBFragment<SystemMsgPresenter, SystemMsgView, FragmentSystemMsgBinding>(), SystemMsgView, IMessageRefresh {

    private lateinit var systemMsgAdapter: SystemMsgAdapter
    private var mPage = 1

    companion object {
        fun getInstance(bundle: Bundle?) = SystemMsgFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentSystemMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = SystemMsgPresenter()

    override fun initView() {
        super.initView()
        systemMsgAdapter = SystemMsgAdapter(R.layout.item_system_msg)
        mBinding.recyclerView.apply {
            adapter = systemMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0))
                }
            })
        }
        mBinding.statusView.loading()
    }

    override fun setOnItemClickListener() {
        systemMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.action_btn) {
                val intent = Intent(context, WebViewActivity::class.java).apply {
                    putExtra(Constant.IntentKey.URL, systemMsgAdapter.data[position].actions[0].redirect)
                }
                startActivity(intent)
            }
            if (view.id == R.id.user_icon) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, systemMsgAdapter.data[position].user_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.getSystemMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getSystemMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getSystemMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onGetSystemMsgSuccess(systemMsgBean: SystemMsgBean) {
        MessageManager.INSTANCE.systemUnreadCount = 0
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))

        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (systemMsgBean.body?.data.isNullOrEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                systemMsgAdapter.setNewData(systemMsgBean.body?.data)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            systemMsgAdapter.addData(systemMsgBean.body.data)
        }

        if (systemMsgBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetSystemMsgError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (systemMsgAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onRefresh() {
        if(isLoad) {
            mBinding.recyclerView.scrollToPosition(0)
            mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        }
    }
}