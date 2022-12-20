package com.scatl.uestcbbs.module.post.view

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseBottomFragment
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common
import com.scatl.uestcbbs.module.post.adapter.PostContentAdapter
import com.scatl.uestcbbs.module.post.presenter.ViewOriginCommentPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.JsonUtil
import com.scatl.uestcbbs.util.TimeUtil

/**
 * Created by tanlei02 on 2022/12/16 16:38
 */
class ViewOriginCommentFragment: BaseBottomFragment<ViewOriginCommentPresenter>() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAvatar: ShapeableImageView
    private lateinit var mName: TextView
    private lateinit var mTime: TextView
    private lateinit var mData: PostDetailBean.ListBean
    private var mTopicId: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = ViewOriginCommentFragment().apply {
            arguments = bundle
        }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mData = bundle.getSerializable(Constant.IntentKey.DATA_1) as PostDetailBean.ListBean
            mTopicId = bundle.getInt(Constant.IntentKey.TOPIC_ID)
        }
    }

    override fun setLayoutResourceId() = R.layout.fragment_view_origin_comment

    override fun findView() {
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mAvatar = view.findViewById(R.id.avatar)
        mName = view.findViewById(R.id.name)
        mTime = view.findViewById(R.id.time)
    }

    override fun initView() {
        val postContentAdapter = PostContentAdapter(mActivity, mTopicId, null)
        val data = JsonUtil.modelListA2B(mData.reply_content, ContentViewBean::class.java, mData.reply_content.size)
        mRecyclerView.adapter = postContentAdapter
        postContentAdapter.data = data

        GlideLoader4Common.simpleLoad(mActivity, mData.icon, mAvatar)
        mName.text = mData.reply_name
        mTime.text = TimeUtil.formatTime(mData.posts_date, R.string.post_time1, mActivity)
            .plus(" ").plus(if (TextUtils.isEmpty(mData.mobileSign)) "网页版" else mData.mobileSign.replace("来自", ""))
    }

    override fun initPresenter() = ViewOriginCommentPresenter()

    override fun setMaxHeightMultiplier() = 0.92
}