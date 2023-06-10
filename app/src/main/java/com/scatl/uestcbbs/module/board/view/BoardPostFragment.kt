package com.scatl.uestcbbs.module.board.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.chip.Chip
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.PostSortByType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentBoardPostBinding
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.module.board.presenter.BoardPostPresenter
import com.scatl.uestcbbs.module.post.adapter.CommonPostAdapter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * created by sca_tl at 2023/6/10 12:06
 */
class BoardPostFragment: BaseVBFragment<BoardPostPresenter, BoardPostView, FragmentBoardPostBinding>(), BoardPostView {

    private lateinit var mCommonPostAdapter: CommonPostAdapter

    private var mBoardId = 0
    private var mFid = 0
    private var mPage = 1
    private var mSortBy = PostSortByType.TYPE_ALL
    private var mNoMoreData = false

    companion object {
        fun getInstance(bundle: Bundle?) = BoardPostFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mBoardId = it.getInt(Constant.IntentKey.BOARD_ID, Int.MAX_VALUE)
            mFid = it.getInt(Constant.IntentKey.FILTER_ID, Int.MAX_VALUE)
            mSortBy = it.getString(Constant.IntentKey.TYPE, PostSortByType.TYPE_ALL)
        }
    }

    override fun getViewBinding() = FragmentBoardPostBinding.inflate(layoutInflater)

    override fun initPresenter() = BoardPostPresenter()

    override fun initView() {
        super.initView()

        bindClickEvent(mBinding.payBtn)

        mCommonPostAdapter = CommonPostAdapter("", onPreload = {
            if (SharePrefUtil.isAutoLoadMore(context) && !mNoMoreData) {
                lazyLoad()
            }
        })
        mBinding.recyclerView.adapter = mCommonPostAdapter
        mBinding.recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)

        mBinding.statusView.loading(mBinding.filterLayout)
    }

    override fun lazyLoad() {
        mPresenter?.getBoardPostList(
            mPage, SharePrefUtil.getPageSize(context), 1,
            mBoardId, mFid, "typeid", mSortBy
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            mBinding.payBtn -> {
                mPresenter?.payForVisiting(mBoardId, SharePrefUtil.getForumHash(context))
            }
        }
    }

    override fun setOnItemClickListener() {
        mCommonPostAdapter.addOnItemChildClickListener(R.id.avatar) { adapter, view, position ->
            val intent = Intent(context, UserDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.USER_ID, mCommonPostAdapter.items[position].user_id)
            }
            startActivity(intent)
        }
        mCommonPostAdapter.addOnItemChildClickListener(R.id.content_layout) { adapter, view, position ->
            val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.TOPIC_ID, mCommonPostAdapter.items[position].topic_id)
            }
            startActivity(intent)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        lazyLoad()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!mCommonPostAdapter.isPreloading) {
            lazyLoad()
        }
    }

    override fun onGetBoardPostSuccess(commonPostBean: CommonPostBean) {
        mCommonPostAdapter.isPreloading = false
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            setFilterView(commonPostBean)
            if (commonPostBean.list.isEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                mCommonPostAdapter.addData(commonPostBean.list, true)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            mCommonPostAdapter.addData(commonPostBean.list, false)
        }

        if (commonPostBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mNoMoreData = true
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetBoardPostError(msg: String?) {
        mCommonPostAdapter.isPreloading = false
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (mCommonPostAdapter.items.isNotEmpty()) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
                if (msg?.contains("您需要支付") == true) {
                    mBinding.payBtn.visibility = View.VISIBLE
                }
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onPaySuccess(msg: String?) {
        mBinding.payBtn.visibility = View.GONE
        lazyLoad()
        showToast(msg, ToastType.TYPE_SUCCESS)
    }

    override fun onPayError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    private fun setFilterView(singleBoardBean: CommonPostBean) {
        if (singleBoardBean.classificationType_list != null && singleBoardBean.classificationType_list.size > 0) {
            mBinding.chipGroup.removeAllViews()
            mBinding.chipGroup.addView(getChip("全部", 0))
            for (i in singleBoardBean.classificationType_list.indices) {
                val chip = getChip(
                    singleBoardBean.classificationType_list[i].classificationType_name,
                    singleBoardBean.classificationType_list[i].classificationType_id
                )
                mBinding.chipGroup.addView(chip)
            }
            mBinding.filterLayout.visibility = View.VISIBLE
        } else {
            mBinding.filterLayout.visibility = View.GONE
        }
    }

    private fun getChip(text: String, filterId: Int): Chip {
        return Chip(ContextThemeWrapper(context, R.style.Widget_Material3_Chip_Filter)).apply {
            this.text = text
            isCheckable = true
            chipStrokeWidth = 0f
            chipCornerRadius = ScreenUtil.dip2px(requireContext(), 25f).toFloat()
            chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#00000000"))
            isChecked = filterId == mFid
            setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                val c = it as Chip
                if (c.isChecked) {
                    mFid = filterId
                    mBinding.recyclerView.scrollToPosition(0)
                    mBinding.refreshLayout.autoRefresh(10, 300, 1f, false)
                } else {
                    c.isChecked = true
                }
            }
        }
    }
}