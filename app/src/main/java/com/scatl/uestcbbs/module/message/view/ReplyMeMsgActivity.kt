package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.view.animation.AnimationUtils
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityReplyMeMsgBinding
import com.scatl.uestcbbs.entity.ReplyMeMsgBean
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity
import com.scatl.uestcbbs.module.message.adapter.ReplyMeMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.ReplyMeMsgPresenter
import com.scatl.uestcbbs.module.post.view.CreateCommentActivity
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/17 10:07
 */
class ReplyMeMsgActivity: BaseVBActivity<ReplyMeMsgPresenter, ReplyMeMsgView, ActivityReplyMeMsgBinding>(), ReplyMeMsgView {

    private lateinit var replyMeMsgAdapter: ReplyMeMsgAdapter
    private var mPage = 1

    override fun getViewBinding() = ActivityReplyMeMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = ReplyMeMsgPresenter()

    override fun initView() {
        super.initView()
        replyMeMsgAdapter = ReplyMeMsgAdapter(R.layout.item_reply_me_msg)
        mBinding.recyclerView.apply {
            adapter = replyMeMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@ReplyMeMsgActivity, R.anim.layout_animation_from_top)
        }
        mBinding.statusView.success()
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_NEW_REPLY_COUNT_ZERO))
    }

    override fun setOnItemClickListener() {
        replyMeMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_reply_me_reply_btn) {
                val intent = Intent(this, CreateCommentActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.data[position].board_id)
                    putExtra(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.data[position].topic_id)
                    putExtra(Constant.IntentKey.QUOTE_ID, replyMeMsgAdapter.data[position].reply_remind_id)
                    putExtra(Constant.IntentKey.IS_QUOTE, true)
                    putExtra(Constant.IntentKey.USER_NAME, replyMeMsgAdapter.data[position].user_name)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_reply_me_user_icon) {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, replyMeMsgAdapter.data[position].user_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_reply_me_quote_rl) {
                val intent = Intent(this, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, replyMeMsgAdapter.data[position].topic_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_reply_me_board_name) {
                val intent = Intent(this, SingleBoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, replyMeMsgAdapter.data[position].board_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getReplyMeMsg(mPage, SharePrefUtil.getPageSize(this@ReplyMeMsgActivity))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getReplyMeMsg(mPage, SharePrefUtil.getPageSize(this@ReplyMeMsgActivity))
    }

    override fun onGetReplyMeMsgSuccess(replyMeMsgBean: ReplyMeMsgBean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            replyMeMsgAdapter.setNewData(replyMeMsgBean.body?.data)
            mBinding.recyclerView.scheduleLayoutAnimation()
        } else {
            replyMeMsgAdapter.addData(replyMeMsgBean.body.data)
        }

        if (replyMeMsgBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetReplyMeMsgError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (replyMeMsgAdapter.data.size != 0) {
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