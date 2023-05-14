package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentViewWarningBinding
import com.scatl.uestcbbs.entity.ViewWarningEntity
import com.scatl.uestcbbs.module.post.adapter.ViewWarningAdapter
import com.scatl.uestcbbs.module.post.presenter.ViewWarningPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ScreenUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * created by sca_tl at 2023/5/2 15:16
 */
class ViewWarningFragment: BaseVBFragment<ViewWarningPresenter, ViewWarningView, FragmentViewWarningBinding>(), ViewWarningView {

    private lateinit var viewWarningAdapter: ViewWarningAdapter
    private var tid: Int = Int.MAX_VALUE
    private var uid: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = ViewWarningFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        tid = bundle?.getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)?:0
        uid = bundle?.getInt(Constant.IntentKey.USER_ID, Int.MAX_VALUE)?:0
    }

    override fun getViewBinding() = FragmentViewWarningBinding.inflate(layoutInflater)

    override fun initPresenter() = ViewWarningPresenter()

    override fun initView() {
        super.initView()
        viewWarningAdapter = ViewWarningAdapter(R.layout.item_view_warning, null)
        mBinding.recyclerView.apply {
            adapter = viewWarningAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 5f)
                }
            })
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableNestedScroll(false)
        mBinding.statusView.loading()
    }

    override fun setOnItemClickListener() {
        viewWarningAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.root_layout) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, viewWarningAdapter.data[position].uid)
                }
                startActivity(intent)
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.viewWarning(tid, uid)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.viewWarning(tid, uid)
    }

    override fun onGetWarningDataSuccess(entity: ViewWarningEntity) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (entity.dsp.isNullOrEmpty()) {
            mBinding.dsp.visibility = View.GONE
        } else {
            mBinding.dsp.visibility = View.VISIBLE
            mBinding.dsp.text = entity.dsp
        }

        if (entity.items.isNullOrEmpty()) {
            mBinding.statusView.error("啊哦，这里空空的~")
        } else {
            viewWarningAdapter.setNewData(entity.items)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetWarningDataError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        if (viewWarningAdapter.data.size != 0) {
            showToast(msg, ToastType.TYPE_ERROR)
        } else {
            mBinding.statusView.error(msg)
        }
    }
}