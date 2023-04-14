package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentDianZanBinding
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.module.post.adapter.DianZanAdapter
import com.scatl.uestcbbs.module.post.presenter.DianZanPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.isNullOrEmpty

/**
 * Created by sca_tl at 2023/4/13 14:08
 */
class DianZanFragment: BaseVBFragment<DianZanPresenter, DianZanView, FragmentDianZanBinding>(), DianZanView {

    private lateinit var dianZanAdapter: DianZanAdapter
    private var tid: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = DianZanFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        tid = bundle?.getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)?: Int.MAX_VALUE
    }

    override fun getViewBinding() = FragmentDianZanBinding.inflate(layoutInflater)

    override fun initPresenter() = DianZanPresenter()

    override fun initView() {
        super.initView()
        dianZanAdapter = DianZanAdapter(R.layout.item_view_voter)
        mBinding.recyclerView.apply {
            adapter = dianZanAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }

        mBinding.refreshLayout.setEnableRefresh(false)
        mBinding.refreshLayout.setEnableNestedScroll(false)
        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getPostDetail(1, 0, 0, tid, 0)
    }

    override fun setOnItemClickListener() {
        dianZanAdapter.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>?, view1: View, position: Int ->
            if (view1.id == R.id.avatar) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, dianZanAdapter.data[position].recommenduid.toInt())
                }
                startActivity(intent)
            }
        }
    }

    override fun onGetPostDetailSuccess(postDetailBean: PostDetailBean) {
        mBinding.statusView.success()
        if (postDetailBean.topic?.zanList?.isNullOrEmpty() == true) {
            mBinding.statusView.error("啊哦，这里空空的~")
        } else {
            dianZanAdapter.setNewData(postDetailBean.topic?.zanList)
            mBinding.recyclerView.scheduleLayoutAnimation()
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetPostDetailError(msg: String?) {
        mBinding.statusView.error(msg)
        mBinding.refreshLayout.finishLoadMore()
    }
}