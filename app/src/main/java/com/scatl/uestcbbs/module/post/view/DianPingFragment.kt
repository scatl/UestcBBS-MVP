package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.PostAppendType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentDianPingBinding
import com.scatl.uestcbbs.entity.PostDianPingBean
import com.scatl.uestcbbs.module.post.adapter.DianPingAdapter
import com.scatl.uestcbbs.module.post.presenter.DianPingPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ScreenUtil.dip2px
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/4/13 9:31
 */
class DianPingFragment: BaseVBFragment<DianPingPresenter, DianPingView, FragmentDianPingBinding>(), DianPingView {

    private lateinit var dianPingAdapter: DianPingAdapter
    private var mPage = 1
    private var tid: Int = Int.MAX_VALUE
    private var pid: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = DianPingFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        tid = bundle?.getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)?:0
        pid = bundle?.getInt(Constant.IntentKey.POST_ID, Int.MAX_VALUE)?:0
    }

    override fun getViewBinding() = FragmentDianPingBinding.inflate(layoutInflater)

    override fun initPresenter() = DianPingPresenter()

    override fun initView() {
        super.initView()
        dianPingAdapter = DianPingAdapter(R.layout.item_dianping, null)
        mBinding.recyclerView.apply {
            adapter = dianPingAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = dip2px(context, 5f)
                }
            })
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableNestedScroll(false)
        mBinding.createBtn.setOnClickListener(this)
        mBinding.statusView.loading()
    }

    override fun onClick(v: View) {
        if (v == mBinding.createBtn) {
            val bundle = Bundle().apply {
                putInt(Constant.IntentKey.TOPIC_ID, tid)
                putInt(Constant.IntentKey.POST_ID, pid)
                putString(Constant.IntentKey.TYPE, PostAppendType.DIANPING)
            }
            PostAppendFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
        }
    }

    override fun setOnItemClickListener() {
        dianPingAdapter.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            if (view.id == R.id.root_layout) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, dianPingAdapter.data[position].uid)
                }
                startActivity(intent)
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.getDianPingList(tid, pid, mPage)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getDianPingList(tid, pid, mPage)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getDianPingList(tid, pid, mPage)
    }

    override fun onGetPostDianPingListSuccess(commentBean: PostDianPingBean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (commentBean.list.isNullOrEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                dianPingAdapter.setNewData(commentBean.list)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            dianPingAdapter.addData(commentBean.list)
        }

        if (commentBean.hasNext) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetPostDianPingListError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (dianPingAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.DIANPING_SUCCESS) {
            mPage = 1
            mPresenter?.getDianPingList(tid, pid, mPage)
        }
    }
}