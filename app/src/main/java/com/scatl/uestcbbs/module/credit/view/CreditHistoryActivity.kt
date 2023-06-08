package com.scatl.uestcbbs.module.credit.view

import android.graphics.Rect
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityCreditHistoryBinding
import com.scatl.uestcbbs.entity.MineCreditBean
import com.scatl.uestcbbs.module.credit.adapter.CreditHistoryAdapter
import com.scatl.uestcbbs.module.credit.presenter.CreditHistoryPresenter
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.widget.span.CustomClickableSpan
import com.scatl.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

class CreditHistoryActivity : BaseVBActivity<CreditHistoryPresenter, CreditHistoryView, ActivityCreditHistoryBinding>(), CreditHistoryView {

    private lateinit var creditHistoryAdapter: CreditHistoryAdapter
    private var mPage = 1
    private var mCurrentInOutSort = 0
    private var mCurrentCreditType = 0

    override fun getViewBinding() = ActivityCreditHistoryBinding.inflate(layoutInflater)

    override fun initPresenter() = CreditHistoryPresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(false)
        creditHistoryAdapter = CreditHistoryAdapter(R.layout.item_credit_history)
        mBinding.recyclerView.apply {
            adapter = creditHistoryAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@CreditHistoryActivity, R.anim.layout_animation_scale_in)
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 10f)
                }
            })
        }

        mBinding.inOutGroup.check(R.id.default_in_out_sort_btn)
        mBinding.creditTypeGroup.check(R.id.default_credit_sort_btn)
        bindClickEvent(
            mBinding.defaultInOutSortBtn, mBinding.inSortBtn, mBinding.outSortBtn,
            mBinding.defaultCreditSortBtn, mBinding.waterSortBtn, mBinding.weiwangSortBtn, mBinding.jiangliquanSortBtn
        )

        mBinding.statusView.success()

        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
    }

    override fun onClick(v: View) {
        when(v) {
            mBinding.defaultInOutSortBtn -> { mCurrentInOutSort = 0 }
            mBinding.inSortBtn -> { mCurrentInOutSort = 1 }
            mBinding.outSortBtn -> { mCurrentInOutSort = -1 }

            mBinding.defaultCreditSortBtn -> { mCurrentCreditType = 0 }
            mBinding.weiwangSortBtn -> { mCurrentCreditType = 1 }
            mBinding.waterSortBtn -> { mCurrentCreditType = 2 }
            mBinding.jiangliquanSortBtn -> { mCurrentCreditType = 6 }
        }
        mPage = 1
        v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        mBinding.recyclerView.layoutManager?.scrollToPosition(0)
    }

    override fun setOnItemClickListener() {
        creditHistoryAdapter.setOnItemClickListener { adapter, view, position ->
            creditHistoryAdapter.data[position].link?.let {
                CustomClickableSpan(getContext(), it).onClick(View(getContext()))
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getCreditHistory(mPage, mCurrentCreditType, mCurrentInOutSort)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getCreditHistory(mPage, mCurrentCreditType, mCurrentInOutSort)
    }

    override fun onGetMineCreditHistorySuccess(creditHistoryBeans: List<MineCreditBean.CreditHistoryBean>, hasNext: Boolean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            creditHistoryAdapter.setNewData(creditHistoryBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        } else {
            creditHistoryAdapter.addData(creditHistoryBeans)
        }

        if (hasNext) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetMineCreditHistoryError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore(false)
        creditHistoryAdapter.setNewData(mutableListOf())
        if (mPage == 1) {
            mBinding.statusView.error(msg)
        }
    }

    override fun getContext() = this

}