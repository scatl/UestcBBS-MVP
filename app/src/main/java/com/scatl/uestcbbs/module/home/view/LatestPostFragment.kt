package com.scatl.uestcbbs.module.home.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.chad.library.adapter.base.QuickAdapterHelper
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.callback.IHomeRefresh
import com.scatl.uestcbbs.databinding.FragmentLatestPostBinding
import com.scatl.uestcbbs.entity.BingPicBean
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.HighLightPostBean
import com.scatl.uestcbbs.entity.NoticeBean
import com.scatl.uestcbbs.manager.ForumListManager
import com.scatl.uestcbbs.module.board.view.BoardActivity
import com.scatl.uestcbbs.module.home.adapter.latestpost.BannerAdapter
import com.scatl.uestcbbs.module.home.adapter.latestpost.FunctionAdapter
import com.scatl.uestcbbs.module.home.adapter.latestpost.HighLightPostAdapter
import com.scatl.uestcbbs.module.home.adapter.latestpost.NoticeAdapter
import com.scatl.uestcbbs.module.home.presenter.LatestPostPresenter
import com.scatl.uestcbbs.module.post.adapter.CommonPostAdapter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.FileUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import java.io.File

/**
 * Created by sca_tl at 2023/6/12 16:45
 */
class LatestPostFragment: BaseVBFragment<LatestPostPresenter, LatestPostView, FragmentLatestPostBinding>(), LatestPostView, IHomeRefresh {

    private var mPage: Int = 1

    private lateinit var helper: QuickAdapterHelper
    private lateinit var commonPostAdapter: CommonPostAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var highLightPostAdapter: HighLightPostAdapter
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var functionAdapter: FunctionAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = LatestPostFragment().apply { arguments = bundle }
    }

    override fun getViewBinding() = FragmentLatestPostBinding.inflate(layoutInflater)

    override fun initPresenter() = LatestPostPresenter()

    override fun initView() {
        super.initView()

        commonPostAdapter = CommonPostAdapter(onPreload = {
            if (SharePrefUtil.isAutoLoadMore(context) && !mBinding.refreshLayout.isRefreshing) {
                onLoadMore(mBinding.refreshLayout)
            }
        })
        bannerAdapter = BannerAdapter()
        highLightPostAdapter = HighLightPostAdapter()
        noticeAdapter = NoticeAdapter()
        functionAdapter = FunctionAdapter(childFragmentManager)
        helper = QuickAdapterHelper.Builder(commonPostAdapter).build()

        mBinding.recyclerView.adapter = helper.adapter
        mBinding.recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
        mBinding.recyclerView.scheduleLayoutAnimation()

        helper.addBeforeAdapter(0, bannerAdapter)
        helper.addBeforeAdapter(1, highLightPostAdapter)
        helper.addBeforeAdapter(2, noticeAdapter)
        helper.addBeforeAdapter(3, functionAdapter)

        highLightPostAdapter.item = HighLightPostBean()
        noticeAdapter.item = NoticeBean()

        try {
            val homeBannerData = FileUtil.readTextFile(File(context?.getExternalFilesDir(Constant.AppPath.JSON_PATH), Constant.FileName.HOME_BANNER_JSON))
            if (JSONObject.isValidObject(homeBannerData)) {
                val jsonObject = JSONObject.parseObject(homeBannerData)
                val bingPicBean = JSONObject.toJavaObject(jsonObject, BingPicBean::class.java)
                bannerAdapter.item = bingPicBean
            }
            val homePostData = FileUtil.readTextFile(File(context?.getExternalFilesDir(Constant.AppPath.JSON_PATH), Constant.FileName.HOME_SIMPLE_POST_JSON))
            if (JSONObject.isValidObject(homePostData)) {
                val jsonObject = JSONObject.parseObject(homePostData)
                val postListBean = JSONObject.toJavaObject(jsonObject, CommonPostBean::class.java)
                commonPostAdapter.submitList(postListBean.list)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun lazyLoad() {
        mBinding.refreshLayout.autoRefresh(10, 300, 1f, false)
    }

    override fun setOnItemClickListener() {
        commonPostAdapter.addOnItemChildClickListener(R.id.board_layout) { adapter, view, position ->
            val parentBoardId = ForumListManager.INSTANCE.getParentForum(commonPostAdapter.items[position].board_id).id

            val intent = Intent(context, BoardActivity::class.java).apply {
                putExtra(Constant.IntentKey.BOARD_ID, parentBoardId)
                putExtra(Constant.IntentKey.LOCATE_BOARD_ID, commonPostAdapter.items[position].board_id)
                putExtra(Constant.IntentKey.BOARD_NAME, commonPostAdapter.items[position].board_name)
            }
            startActivity(intent)
        }

        commonPostAdapter.addOnItemChildClickListener(R.id.avatar) { adapter, view, position ->
            val intent = Intent(context, UserDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.USER_ID, commonPostAdapter.items[position].user_id)
            }
            startActivity(intent)
        }

        commonPostAdapter.addOnItemChildClickListener(R.id.content_layout) { adapter, view, position ->
            val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.TOPIC_ID, commonPostAdapter.items[position].topic_id)
            }
            startActivity(intent)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getHighLightPost()
        mPresenter?.getBannerData()
        mPresenter?.getNotice()
        mPresenter?.getSimplePostList(1, SharePrefUtil.getPageSize(context), "new")
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!commonPostAdapter.isPreloading) {
            mPresenter?.getSimplePostList(mPage, SharePrefUtil.getPageSize(context), "new")
        }
    }

    override fun getBannerDataSuccess(bingPicBean: BingPicBean) {
        if (bannerAdapter.item != bingPicBean) {
            bannerAdapter.item = bingPicBean
        }
        FileUtil.saveStringToFile(JSON.toJSONString(bingPicBean),
            File(context?.getExternalFilesDir(Constant.AppPath.JSON_PATH), Constant.FileName.HOME_BANNER_JSON))
    }

    override fun getPostListSuccess(postListBean: CommonPostBean) {
        commonPostAdapter.isPreloading = false
        mBinding.refreshLayout.finishRefresh()

        if (mPage == 1) {
            FileUtil.saveStringToFile(JSON.toJSONString(postListBean),
                File(context?.getExternalFilesDir(Constant.AppPath.JSON_PATH), Constant.FileName.HOME_SIMPLE_POST_JSON))

            if (postListBean.list.isNotEmpty()) {
                commonPostAdapter.addData(postListBean.list, true)
                mBinding.recyclerView.scheduleLayoutAnimation()
            }
        } else {
            commonPostAdapter.addData(postListBean.list, false)
        }

        if (postListBean.has_next == 1) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            commonPostAdapter.noMoreData = true
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun getPostListError(msg: String?) {
        commonPostAdapter.isPreloading = false
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            showToast(msg, ToastType.TYPE_ERROR)
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onGetNoticeSuccess(noticeBean: NoticeBean) {
        noticeAdapter.item = noticeBean
    }

    override fun onGetHighLightPostSuccess(highLightPostBean: HighLightPostBean) {
        highLightPostAdapter.item = highLightPostBean
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_BANNER_VISIBILITY_CHANGE) {
            mBinding.recyclerView.scrollToPosition(0)
            mBinding.refreshLayout.autoRefresh(10, 300, 1f, false)
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.ALL_SITE_TOP_STICK_VISIBILITY_CHANGE) {
            highLightPostAdapter.notifyItemChanged(0, HighLightPostAdapter.PAY_LOAD_CHANGE_VISIBILITY)
        }
    }

    override fun onRefresh() {
        mBinding.recyclerView.scrollToPosition(0)
        mBinding.refreshLayout.autoRefresh(10, 300, 1f, false)
    }
}