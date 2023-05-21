package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IMessageRefresh
import com.scatl.uestcbbs.databinding.FragmentPrivateMsgBinding
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.message.adapter.PrivateMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.PrivateMsgPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.ToastUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/3/16 10:00
 */
class PrivateMsgFragment: BaseVBFragment<PrivateMsgPresenter, PrivateMsgView, FragmentPrivateMsgBinding>(), PrivateMsgView, IMessageRefresh {

    private var mPage = 1
    private lateinit var privateMsgAdapter: PrivateMsgAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = PrivateMsgFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentPrivateMsgBinding.inflate(layoutInflater)
    override fun initPresenter() = PrivateMsgPresenter()

    override fun initView() {
        super.initView()
        privateMsgAdapter = PrivateMsgAdapter(R.layout.item_private_msg)
        mBinding.recyclerView.apply {
            adapter = privateMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0))
                }
            })
        }
        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        mPresenter?.getPrivateMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun setOnItemClickListener() {
        privateMsgAdapter.setOnItemClickListener { adapter, view, position ->
            if (view.id == R.id.root_layout) {
                val intent = Intent(context, PrivateChatActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.data[position].toUserId)
                    putExtra(Constant.IntentKey.USER_NAME, privateMsgAdapter.data[position].toUserName)
                    putExtra(Constant.IntentKey.IS_NEW_CONTENT, privateMsgAdapter.data[position].isNew == 1)
                }
                startActivity(intent)
                privateMsgAdapter.data[position].isNew = 0
                privateMsgAdapter.notifyItemChanged(position)

                MessageManager.INSTANCE.decreasePmCount()
                EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
            }
        }

        privateMsgAdapter.setOnItemLongClickListener { adapter, view, position ->
            mPresenter?.showDeletePrivateMsgDialog(
                privateMsgAdapter.data[position].toUserName,
                privateMsgAdapter.data[position].toUserId,
                position
            )
            false
        }

        privateMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.user_icon) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, privateMsgAdapter.data[position].toUserId)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getPrivateMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getPrivateMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onGetPrivateMsgSuccess(privateMsgBean: PrivateMsgBean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (privateMsgBean.body.list.isNullOrEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                privateMsgAdapter.setNewData(privateMsgBean.body.list)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            privateMsgAdapter.addData(privateMsgBean.body.list)
        }

        if (privateMsgBean.body.hasNext == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetPrivateMsgError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (privateMsgAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onDeletePrivateMsgSuccess(msg: String?, position: Int) {
        try {
            privateMsgAdapter.data.removeAt(position)
            privateMsgAdapter.notifyItemRemoved(position + privateMsgAdapter.headerLayoutCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ToastUtil.showToast(context, msg, ToastType.TYPE_SUCCESS)
    }

    override fun onDeletePrivateMsgError(msg: String?) {
        ToastUtil.showToast(context, msg, ToastType.TYPE_ERROR)
    }

    override fun onRefresh() {
        if(isLoad) {
            mBinding.recyclerView.scrollToPosition(0)
            mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        }
    }
}