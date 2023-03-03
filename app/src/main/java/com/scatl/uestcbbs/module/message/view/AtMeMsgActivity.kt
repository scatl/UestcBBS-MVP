package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.view.animation.AnimationUtils
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityAtMeMsgBinding
import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity
import com.scatl.uestcbbs.module.message.adapter.AtMeMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.AtMeMsgPresenter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/17 14:08
 */
class AtMeMsgActivity: BaseVBActivity<AtMeMsgPresenter, AtMeMsgView, ActivityAtMeMsgBinding>(), AtMeMsgView {

    private lateinit var atMeMsgAdapter: AtMeMsgAdapter

    private var mPage = 1

    override fun getViewBinding() = ActivityAtMeMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = AtMeMsgPresenter()

    override fun initView() {
        super.initView()
        atMeMsgAdapter = AtMeMsgAdapter(R.layout.item_at_me_msg)
        mBinding.recyclerView.apply {
            adapter = atMeMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@AtMeMsgActivity, R.anim.layout_animation_from_top)
        }

        mBinding.statusView.success()
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_NEW_AT_COUNT_ZERO))
    }

    override fun setOnItemClickListener() {
        atMeMsgAdapter.setOnItemClickListener { adapter, view, position ->
            if (view.id == R.id.item_at_me_cardview) {
                val intent = Intent(this, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, atMeMsgAdapter.data[position].topic_id)
                }
                startActivity(intent)
            }
        }

        atMeMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_at_me_icon) {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, atMeMsgAdapter.data[position].user_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_at_me_board_name) {
                val intent = Intent(this, SingleBoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, atMeMsgAdapter.data[position].board_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getAtMeMsg(mPage, SharePrefUtil.getPageSize(this@AtMeMsgActivity))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getAtMeMsg(mPage, SharePrefUtil.getPageSize(this@AtMeMsgActivity))
    }

    override fun onGetAtMeMsgSuccess(atMsgBean: AtMsgBean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            atMeMsgAdapter.setNewData(atMsgBean.body.data)
            mBinding.recyclerView.scheduleLayoutAnimation()
        } else {
            atMeMsgAdapter.addData(atMsgBean.body.data)
        }

        if (atMsgBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetAtMeMsgError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (atMeMsgAdapter.data.size != 0) {
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