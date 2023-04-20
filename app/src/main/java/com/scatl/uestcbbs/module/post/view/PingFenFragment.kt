package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentPingFenBinding
import com.scatl.uestcbbs.entity.RateUserBean
import com.scatl.uestcbbs.module.post.adapter.PingFenAdapter
import com.scatl.uestcbbs.module.post.presenter.PingFenPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/4/20 16:20
 */
class PingFenFragment: BaseVBFragment<PingFenPresenter, PingFenView, FragmentPingFenBinding>(), PingFenView {

    override fun getViewBinding() = FragmentPingFenBinding.inflate(layoutInflater)

    override fun initPresenter() = PingFenPresenter()

    private lateinit var pingFenAdapter: PingFenAdapter
    private var tid: Int = Int.MAX_VALUE
    private var pid: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = PingFenFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        tid = bundle?.getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)?:0
        pid = bundle?.getInt(Constant.IntentKey.POST_ID, Int.MAX_VALUE)?:0
    }

    override fun initView() {
        super.initView()
        pingFenAdapter = PingFenAdapter(R.layout.item_ping_fen)
        mBinding.recyclerView.apply {
            adapter = pingFenAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 5f)
                }
            })
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableNestedScroll(false)
        mBinding.rateBtn.setOnClickListener(this)
        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getRateUser(tid, pid)
    }

    override fun onClick(v: View) {
        if (v == mBinding.rateBtn) {
            val bundle = Bundle()
            bundle.putInt(Constant.IntentKey.TOPIC_ID, tid)
            bundle.putInt(Constant.IntentKey.POST_ID, pid)
            PostRateFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
        }
    }

    override fun setOnItemClickListener() {
        pingFenAdapter.setOnItemChildClickListener { adapter, view1, position ->
            if (view1.id == R.id.root_layout) {
                val intent = Intent(context, UserDetailActivity::class.java)
                intent.putExtra(Constant.IntentKey.USER_ID, pingFenAdapter.data[position].uid)
                startActivity(intent)
            }
        }
    }

    override fun onGetRateUserSuccess(rateUserBeans: List<RateUserBean>) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (rateUserBeans.isEmpty()) {
            mBinding.statusView.error("啊哦，这里空空的~")
        } else {
            pingFenAdapter.setNewData(rateUserBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetRateUserError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (pingFenAdapter.data.size != 0) {
            showToast(msg, ToastType.TYPE_ERROR)
        } else {
            mBinding.statusView.error(msg)
        }
        mBinding.refreshLayout.finishLoadMore()
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.RATE_SUCCESS) {
            mPresenter?.getRateUser(tid, pid)
        }
    }
}