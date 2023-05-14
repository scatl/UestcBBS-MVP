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
import com.scatl.uestcbbs.databinding.FragmentAtMeMsgBinding
import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.manager.ForumListManager
import com.scatl.uestcbbs.module.board.view.BoardActivity
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.message.adapter.AtMeMsgAdapter
import com.scatl.uestcbbs.module.message.presenter.AtMeMsgPresenter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/2/17 14:08
 */
class AtMeMsgFragment: BaseVBFragment<AtMeMsgPresenter, AtMeMsgView, FragmentAtMeMsgBinding>(), AtMeMsgView, IMessageRefresh {

    private lateinit var atMeMsgAdapter: AtMeMsgAdapter
    private var mPage = 1

    companion object {
        fun getInstance(bundle: Bundle?) = AtMeMsgFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentAtMeMsgBinding.inflate(layoutInflater)

    override fun initPresenter() = AtMeMsgPresenter()

    override fun initView() {
        super.initView()
        atMeMsgAdapter = AtMeMsgAdapter(R.layout.item_at_me_msg)
        mBinding.recyclerView.apply {
            adapter = atMeMsgAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0))
                }
            })
        }

        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getAtMeMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun setOnItemClickListener() {
        atMeMsgAdapter.setOnItemClickListener { adapter, view, position ->
            if (view.id == R.id.root_layout) {
                val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, atMeMsgAdapter.data[position].topic_id)
                    putExtra(Constant.IntentKey.LOCATE_COMMENT, Bundle().also {
                        it.putInt(Constant.IntentKey.POST_ID, atMeMsgAdapter.data[position].reply_remind_id)
                    })
                }
                startActivity(intent)
            }
        }

        atMeMsgAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.user_icon) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, atMeMsgAdapter.data[position].user_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.board_name) {
                val intent = Intent(context, BoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, ForumListManager.INSTANCE.getParentForum(atMeMsgAdapter.data[position].board_id).id)
                    putExtra(Constant.IntentKey.LOCATE_BOARD_ID, atMeMsgAdapter.data[position].board_id)
                    putExtra(Constant.IntentKey.BOARD_NAME, atMeMsgAdapter.data[position].board_name)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getAtMeMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getAtMeMsg(mPage, SharePrefUtil.getPageSize(context))
    }

    override fun onGetAtMeMsgSuccess(atMsgBean: AtMsgBean) {
        MessageManager.INSTANCE.atUnreadCount = 0
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))

        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (atMsgBean.body.data.isNullOrEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                atMeMsgAdapter.setNewData(atMsgBean.body.data)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
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

    override fun onRefresh() {
        if(isLoad) {
            mBinding.recyclerView.scrollToPosition(0)
            mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        }
    }
}