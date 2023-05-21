package com.scatl.uestcbbs.module.collection.view

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentCollectionListBinding
import com.scatl.uestcbbs.entity.CollectionListBean
import com.scatl.uestcbbs.module.collection.adapter.CollectionListAdapter
import com.scatl.uestcbbs.module.collection.presenter.CollectionListPresenter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/5/5 11:41
 */
class CollectionListFragment: BaseVBFragment<CollectionListPresenter, CollectionListView, FragmentCollectionListBinding>(), CollectionListView {

    private lateinit var collectionListAdapter: CollectionListAdapter
    private var mType = TYPE_ALL
    private var mOrder = ORDER_FOLLOW_NUM
    private var mPage = 1

    companion object {
        const val TYPE_MINE = "my"
        const val TYPE_RECOMMEND = "recommend"
        const val TYPE_ALL = "all"

        const val ORDER_CREATE_TIME = "createtime"
        const val ORDER_FOLLOW_NUM = "follownum"
        const val ORDER_THREAD_NUM = "threadnum"
        const val ORDER_COMMENT_NUM = "commentnum"

        fun getInstance(bundle: Bundle?) = CollectionListFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentCollectionListBinding.inflate(layoutInflater)

    override fun initPresenter() = CollectionListPresenter()

    override fun getBundle(bundle: Bundle?) {
        super.getBundle(bundle)
        mType = bundle?.getString(Constant.IntentKey.TYPE, TYPE_ALL)?: TYPE_ALL
    }

    override fun initView() {
        super.initView()
        collectionListAdapter = CollectionListAdapter(R.layout.item_collection_list)

        if (mType == TYPE_ALL) {
            mBinding.chipGroup.visibility = View.VISIBLE
            mBinding.commentNumSortBtn.setOnClickListener(this)
            mBinding.createTimeSortBtn.setOnClickListener(this)
            mBinding.followNumSortBtn.setOnClickListener(this)
            mBinding.postNumSortBtn.setOnClickListener(this)
            mBinding.followNumSortBtn.isChecked = true
        } else {
            mBinding.chipGroup.visibility = View.GONE
        }

        mBinding.recyclerView.apply {
            adapter = collectionListAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.HOME_NAVIGATION_HIDE, dy > 0))
                }
            })
        }
        mBinding.statusView.loading()
    }

    override fun onClick(v: View) {
        if (v == mBinding.followNumSortBtn || v == mBinding.postNumSortBtn || v == mBinding.createTimeSortBtn || v == mBinding.commentNumSortBtn) {
            val order = when (v) {
                mBinding.followNumSortBtn -> { ORDER_FOLLOW_NUM }
                mBinding.postNumSortBtn -> { ORDER_THREAD_NUM }
                mBinding.createTimeSortBtn -> { ORDER_CREATE_TIME }
                mBinding.commentNumSortBtn -> { ORDER_COMMENT_NUM }
                else -> { ORDER_FOLLOW_NUM }
            }
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            mPage = 1
            mBinding.statusView.loading()
            collectionListAdapter.setNewData(ArrayList())
            mPresenter?.getCollectionList(mPage, mType, order)
        }
    }

    override fun setOnItemClickListener() {
        collectionListAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(context, CollectionDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.COLLECTION_ID, collectionListAdapter.data[position].collectionId)
                putExtra(Constant.IntentKey.IS_NEW_CONTENT, collectionListAdapter.data[position].hasUnreadPost)
            }
            startActivity(intent)
        }

        collectionListAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.latest_post_title) {
                val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, collectionListAdapter.data[position].latestPostId)
                }
                startActivity(intent)
            }
            if (view.id == R.id.avatar || view.id == R.id.author_name) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, collectionListAdapter.data[position].authorId)
                }
                startActivity(intent)
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.getCollectionList(mPage, mType, mOrder)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getCollectionList(mPage, mType, mOrder)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getCollectionList(mPage, mType, mOrder)
    }

    override fun onGetCollectionListSuccess(collectionListBeans: List<CollectionListBean>, hasNext: Boolean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            if (collectionListBeans.isEmpty()) {
                mBinding.statusView.error("啊哦，这里空空的~")
            } else {
                collectionListAdapter.setNewData(collectionListBeans)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            collectionListAdapter.addData(collectionListBeans)
        }

        if (hasNext) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetCollectionListError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (collectionListAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }
}