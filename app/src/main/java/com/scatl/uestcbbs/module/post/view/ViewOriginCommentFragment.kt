package com.scatl.uestcbbs.module.post.view

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseBottomFragment
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.databinding.FragmentViewOriginCommentBinding
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common
import com.scatl.uestcbbs.module.post.adapter.PostContentAdapter
import com.scatl.uestcbbs.module.post.presenter.ViewOriginCommentPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.JsonUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl on 2022/12/16 16:38
 */
class ViewOriginCommentFragment: BaseVBBottomFragment<ViewOriginCommentPresenter, ViewOriginCommentView, FragmentViewOriginCommentBinding>(), ViewOriginCommentView {

    private lateinit var mData: PostDetailBean.ListBean
    private var mTopicId: Int = Int.MAX_VALUE

    companion object {
        fun getInstance(bundle: Bundle?) = ViewOriginCommentFragment().apply {
            arguments = bundle
        }
    }

    override fun getViewBinding() = FragmentViewOriginCommentBinding.inflate(layoutInflater)

    override fun initPresenter() = ViewOriginCommentPresenter()

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mData = bundle.getSerializable(Constant.IntentKey.DATA_1) as PostDetailBean.ListBean
            mTopicId = bundle.getInt(Constant.IntentKey.TOPIC_ID)
        }
    }

    override fun initView() {
        val postContentAdapter = PostContentAdapter(requireContext(), mTopicId, null)
        val data = JsonUtil.modelListA2B(mData.reply_content, ContentViewBean::class.java, mData.reply_content.size)
        mBinding.recyclerView.adapter = postContentAdapter
        postContentAdapter.data = data
        postContentAdapter.type = PostContentAdapter.TYPE.VIEW_ORIGIN

        mBinding.avatar.load(mData.icon)
        mBinding.name.text = mData.reply_name
        mBinding.time.text = TimeUtil.formatTime(mData.posts_date, R.string.post_time1, context)
            .plus(" ").plus(if (TextUtils.isEmpty(mData.mobileSign)) "网页版" else mData.mobileSign.replace("来自", ""))
    }

    override fun setMaxHeightMultiplier() = 0.92
}