package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IHomeRefresh
import com.scatl.uestcbbs.databinding.FragmentCommonPostBinding
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.manager.ForumListManager
import com.scatl.uestcbbs.module.board.view.BoardActivity
import com.scatl.uestcbbs.module.post.adapter.CommonPostAdapter
import com.scatl.uestcbbs.module.post.presenter.CommonPostPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/4/26 10:08
 */
class CommonPostFragment: BaseVBFragment<CommonPostPresenter, CommonPostView, FragmentCommonPostBinding>(), CommonPostView, IHomeRefresh {

    private var mType: String = TYPE_BOARD_POST
    private var mUid: Int = Int.MAX_VALUE
    private var mPage: Int = 1
    private var mNoMoreData = false
    private lateinit var commonPostAdapter: CommonPostAdapter

    companion object {
        const val TYPE_USER_POST = "user_post"
        const val TYPE_USER_REPLY = "user_reply"
        const val TYPE_USER_FAVORITE = "user_favorite"
        const val TYPE_BOARD_POST = "board_post"
        const val TYPE_HOT_POST = "hot_post"
        const val TYPE_ESSENCE_POST = "essence_post"
        const val TYPE_NEW_REPLY_POST = "new_reply_post"

        fun getInstance(bundle: Bundle?) = CommonPostFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentCommonPostBinding.inflate(layoutInflater)

    override fun initPresenter() = CommonPostPresenter()

    override fun getBundle(bundle: Bundle?) {
        mType = bundle?.getString(Constant.IntentKey.TYPE, TYPE_BOARD_POST)?: TYPE_BOARD_POST
        mUid = bundle?.getInt(Constant.IntentKey.USER_ID, Int.MAX_VALUE)?: Int.MAX_VALUE
    }

    override fun initView() {
        super.initView()
        commonPostAdapter = CommonPostAdapter(R.layout.item_common_post, mType, onPreload = {
            if (SharePrefUtil.isAutoLoadMore(context) && !mNoMoreData) {
                lazyLoad()
            }
        })
        mBinding.recyclerView.apply {
            adapter = commonPostAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
        }

        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        when(mType) {
            TYPE_USER_POST -> {
                mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "topic")
            }
            TYPE_USER_REPLY -> {
                mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "reply")
            }
            TYPE_USER_FAVORITE -> {
                mPresenter?.userPost(mPage, SharePrefUtil.getPageSize(context), mUid, "favorite")
            }
            TYPE_HOT_POST -> {
                mPresenter?.getHotPostList(mPage, SharePrefUtil.getPageSize(context))
            }
            TYPE_ESSENCE_POST -> {
                mPresenter?.getHomeTopicList(mPage, SharePrefUtil.getPageSize(context), "essence")
            }
            TYPE_NEW_REPLY_POST -> {
                mPresenter?.getHomeTopicList(mPage, SharePrefUtil.getPageSize(context), "all")
            }
        }
    }

    override fun setOnItemClickListener() {
        commonPostAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.board_name) {

                val parentBoardId = ForumListManager.INSTANCE.getParentForum(commonPostAdapter.data[position].board_id).id

                val intent = Intent(context, BoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, parentBoardId)
                    putExtra(Constant.IntentKey.LOCATE_BOARD_ID, commonPostAdapter.data[position].board_id)
                    putExtra(Constant.IntentKey.BOARD_NAME, commonPostAdapter.data[position].board_name)
                }

                startActivity(intent)
            }

            if (view.id == R.id.avatar) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, commonPostAdapter.data[position].user_id)
                }
                startActivity(intent)
            }

            if (view.id == R.id.content_layout) {
                val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, commonPostAdapter.data[position].topic_id)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        lazyLoad()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!commonPostAdapter.isPreloading) {
            lazyLoad()
        }
    }

    override fun onGetPostSuccess(commonPostBean: CommonPostBean) {
        commonPostAdapter.isPreloading = false
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (commonPostBean.list.isEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                commonPostAdapter.addData(commonPostBean.list, true)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            commonPostAdapter.addData(commonPostBean.list, false)
        }

        if (commonPostBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mNoMoreData = true
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetPostError(msg: String?) {
        commonPostAdapter.isPreloading = false
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (commonPostAdapter.data.size != 0) {
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
        mBinding.recyclerView.scrollToPosition(0)
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
    }
}