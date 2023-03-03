package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.view.animation.AnimationUtils
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivitySystemMsgBinding
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.module.message.adapter.SystemMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.SystemMsgPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.module.webview.view.WebViewActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/20 10:26
 */
class SystemMsgActivity: BaseVBActivity<SystemMsgPresenter, SystemMsgView, ActivitySystemMsgBinding>(), SystemMsgView {

    private lateinit var systemMsgAdapter: SystemMsgAdapter

    private var mPage = 1

    override fun getViewBinding() = ActivitySystemMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = SystemMsgPresenter()

    override fun initView() {
        super.initView()
        systemMsgAdapter = SystemMsgAdapter(R.layout.item_system_msg)
        mBinding.recyclerView.apply {
            adapter = systemMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@SystemMsgActivity, R.anim.layout_animation_from_top)
        }
        mBinding.statusView.success()
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_NEW_SYSTEM_MSG_ZERO))
    }

    override fun setOnItemClickListener() {
        systemMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_system_action_btn) {
                val intent = Intent(this@SystemMsgActivity, WebViewActivity::class.java).apply {
                    putExtra(Constant.IntentKey.URL, systemMsgAdapter.data[position].actions[0].redirect)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_system_msg_user_icon) {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, systemMsgAdapter.data[position].user_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getSystemMsg(mPage, SharePrefUtil.getPageSize(this@SystemMsgActivity),)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getSystemMsg(mPage, SharePrefUtil.getPageSize(this@SystemMsgActivity))
    }

    override fun onGetSystemMsgSuccess(systemMsgBean: SystemMsgBean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            systemMsgAdapter.setNewData(systemMsgBean.body?.data)
            mBinding.recyclerView.scheduleLayoutAnimation()
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

    override fun getContext() = this
}