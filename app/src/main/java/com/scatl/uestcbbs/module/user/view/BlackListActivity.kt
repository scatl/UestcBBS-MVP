package com.scatl.uestcbbs.module.user.view

import android.content.Intent
import android.graphics.Rect
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityBlackListBinding
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.module.user.adapter.BlackListAdapter
import com.scatl.uestcbbs.module.user.presenter.BlackListPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/4/25 14:54
 */
class BlackListActivity: BaseVBActivity<BlackListPresenter, BlackListView, ActivityBlackListBinding>(), BlackListView {

    private lateinit var blackListAdapter: BlackListAdapter

    override fun getViewBinding() = ActivityBlackListBinding.inflate(layoutInflater)

    override fun initPresenter() = BlackListPresenter()

    override fun getContext() = this

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        blackListAdapter = BlackListAdapter(R.layout.item_black_list)
        mBinding.recyclerView.apply {
            adapter = blackListAdapter
            layoutManager = GridLayoutManager(context, 3)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 10f)
                }
            })
        }

        mBinding.updateTime.text = "上次更新时间：".plus(TimeUtil.getFormatDate(BlackListManager.INSTANCE.updateTime, "yyyy/MM/dd HH:mm"))
        blackListAdapter.setNewData(BlackListManager.INSTANCE.blackList)
    }

    override fun setOnItemClickListener() {
        blackListAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra(Constant.IntentKey.USER_ID, blackListAdapter.data[position].uid)
            startActivity(intent)
        }
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.BLACK_LIST_DATA_CHANGED) {
            blackListAdapter.setNewData(BlackListManager.INSTANCE.blackList)
        }
    }
}