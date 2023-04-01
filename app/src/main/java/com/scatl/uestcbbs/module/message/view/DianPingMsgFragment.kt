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
import com.scatl.uestcbbs.databinding.FragmentDianPingMessageBinding
import com.scatl.uestcbbs.entity.DianPingMessageBean
import com.scatl.uestcbbs.module.message.adapter.DianPingMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.DianPingMsgPresenter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.post.view.ViewDianPingFragment
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/17 10:31
 */
class DianPingMsgFragment: BaseVBFragment<DianPingMsgPresenter, DianPingMsgView, FragmentDianPingMessageBinding>(), DianPingMsgView, IMessageRefresh {

    private lateinit var dianPingMsgAdapter: DianPingMsgAdapter
    private var mPage = 1

    companion object {
        fun getInstance(bundle: Bundle?) = DianPingMsgFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentDianPingMessageBinding.inflate(layoutInflater)

    override fun initPresenter() = DianPingMsgPresenter()

    override fun initView() {
        super.initView()
        dianPingMsgAdapter = DianPingMsgAdapter(R.layout.item_dianping_msg)
        mBinding.recyclerView.apply {
            adapter = dianPingMsgAdapter
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
        dianPingMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.view_dianping_btn) {
//                val intent = Intent(context, NewPostDetailActivity::class.java).apply {
//                    putExtra(Constant.IntentKey.TOPIC_ID, dianPingMsgAdapter.data[position].tid)
//                    putExtra(Constant.IntentKey.LOCATED_PID, dianPingMsgAdapter.data[position].pid)
//                }
//                startActivity(intent)
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, dianPingMsgAdapter.data[position].tid)
                    putInt(Constant.IntentKey.POST_ID, dianPingMsgAdapter.data[position].pid)
                    putBoolean(Constant.IntentKey.DATA_1, true)
                }
                ViewDianPingFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.getDianPingMsg(mPage)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getDianPingMsg(mPage)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getDianPingMsg(mPage)
    }

    override fun onGetDianPingMessageSuccess(dianPingMessageBean: List<DianPingMessageBean>, hasNext: Boolean) {
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.CLEAR_DIANPING_MSG_COUNT))

        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (dianPingMessageBean.isEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                dianPingMsgAdapter.setNewData(dianPingMessageBean)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            dianPingMsgAdapter.addData(dianPingMessageBean)
        }

        if (hasNext) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetDianPingMessageError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (dianPingMsgAdapter.data.size != 0) {
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