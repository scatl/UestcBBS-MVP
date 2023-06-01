package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SupportedBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.module.post.view.ViewOriginCommentFragment
import com.scatl.uestcbbs.util.CommentUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.ForumUtil
import com.scatl.uestcbbs.util.JsonUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import org.litepal.LitePal
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/5/19 17:16
 */
@SuppressLint("SetTextI18n")
class PostCommentAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<PostDetailBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    var authorId: Int = 0
    var topicId: Int = 0
    var totalCommentData: List<PostDetailBean.ListBean>? = null

    companion object {
        const val UPDATE_SUPPORT = "update_support"
        const val UPDATE_AWARD_INFO = "update_award_info"
    }

    fun addData(newData: List<PostDetailBean.ListBean>, reload: Boolean) {
        val filterData = newData.filter {
            !BlackListManager.INSTANCE.isBlacked(it.reply_id)
        }
        if (reload) {
            setNewData(filterData)
        } else {
            addData(filterData)
        }
    }

    override fun convertPayloads(helper: BaseViewHolder, item: PostDetailBean.ListBean, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            convert(helper, item)
        } else {
            if (payloads[0] is String) {
                if (UPDATE_SUPPORT == payloads[0]) {
                    item.isSupported = true
                    item.supportedCount++
                    item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(mContext)
                    val supportedBean = SupportedBean()
                    supportedBean.pid = item.reply_posts_id
                    supportedBean.save()
                    updateSupport(helper, item)
                    updateHotImg(helper, item)
                }
            } else if (payloads[0] is Bundle) {
                val key = (payloads[0] as Bundle).getString("key")
                if (key == UPDATE_AWARD_INFO) {
                    val awardInfo = (payloads[0] as Bundle).getString("info")
                    item.awardInfo = awardInfo
                    updateAwardInfo(helper, item)
                }
            }

        }
    }

    override fun convert(helper: BaseViewHolder, item: PostDetailBean.ListBean) {
        super.convert(helper, item)
        helper
            .addOnClickListener(R.id.btn_reply)
            .addOnClickListener(R.id.reply_avatar)
            .addOnClickListener(R.id.btn_support)
            .addOnClickListener(R.id.btn_more)
            .addOnClickListener(R.id.root_layout)
            .addOnClickListener(R.id.quote_layout)
            .addOnLongClickListener(R.id.root_layout)

        val replyName = helper.getView<TextView>(R.id.reply_name)
        val postTime = helper.getView<TextView>(R.id.post_time)
        val replyAvatar = helper.getView<ImageView>(R.id.reply_avatar)
        val authorTag = helper.getView<TextView>(R.id.author_tag)
        val floor = helper.getView<TextView>(R.id.floor)
        val mobileSign = helper.getView<TextView>(R.id.mobile_sign)
        val level = helper.getView<TextView>(R.id.reply_level)
        val quoteLayout = helper.getView<View>(R.id.quote_layout)
        val quoteName = helper.getView<TextView>(R.id.quote_name)
        val quoteAvatar = helper.getView<ShapeableImageView>(R.id.quote_avatar)
        val backUpQuoteLayout = helper.getView<View>(R.id.back_up_quote_layout)
        val backUpQuoteName = helper.getView<TextView>(R.id.back_up_quote_name)
        val backUpQuoteContent = helper.getView<TextView>(R.id.back_up_quote_content)
        val quoteCommentRv = helper.getView<RecyclerView>(R.id.origin_comment_rv)
        val btnViewOriginComment = helper.getView<View>(R.id.btn_view_full_quote)
        val contentRv = helper.getView<RecyclerView>(R.id.content_rv)

        if (item.reply_id == 0 && "匿名" == item.reply_name) {
            replyAvatar.load(R.drawable.ic_anonymous)
        } else {
            replyAvatar.load(item.icon)
        }

        replyName.text = item.reply_name
        postTime.text = TimeUtil.formatTime(item.posts_date, R.string.post_time1, mContext)
        authorTag.visibility = if (item.reply_id == authorId && item.reply_id != 0) View.VISIBLE else View.GONE
        mobileSign.text = if (item.mobileSign.isNullOrEmpty()) "网页版" else item.mobileSign.replace("来自", "")

        floor.text = if (item.position in 2..5) Constant.FLOOR[item.position - 2] else "#${item.position}"
        if (item.poststick == 1) {
            floor.text = "置顶"
            floor.setBackgroundResource(R.drawable.shape_post_detail_user_level_1)
        } else {
            floor.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
            floor.background = null
        }

        if (!item.extraPanel.isNullOrEmpty()) {
            if ("support" == item.extraPanel[0].type) {
                item.supportedCount = item.extraPanel[0].extParams?.recommendAdd?:0
            }
        }
        item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(mContext)
        item.isSupported = null != LitePal.where("pid = " + item.reply_posts_id).findFirst(SupportedBean::class.java)

        updateSupport(helper, item)
        updateHotImg(helper, item)
        updateAwardInfo(helper, item)

        if (!item.userTitle.isNullOrEmpty()) {
            level.visibility = View.VISIBLE
            val matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle)
            level.backgroundTintList = ColorStateList.valueOf(ForumUtil.getLevelColor(mContext, item.userTitle))
            level.text = if (matcher.find()) {
                if (matcher.group(2)?.contains("禁言") == true) { "禁言中" } else { matcher.group(2) }
            } else {
                item.userTitle
            }
        } else {
            level.visibility = View.GONE
        }

        contentRv.adapter = PostContentAdapter(mContext, topicId, null).apply {
            type = PostContentAdapter.TYPE.REPLY
            this.data = JsonUtil.modelListA2B(item.reply_content, ContentViewBean::class.java, item.reply_content.size)
            comments = totalCommentData
        }

        //有引用内容
        if (item.is_quote == 1) {
            val data = CommentUtil.findCommentByPid(totalCommentData, item.quote_pid)
            if (data != null) {
                quoteLayout.visibility = View.VISIBLE
                backUpQuoteLayout.visibility = View.GONE
                quoteName.text = data.reply_name
                if (data.reply_id == 0 && "匿名" == data.reply_name) {
                    quoteAvatar.load(R.drawable.ic_anonymous)
                } else {
                    quoteAvatar.load(data.icon)
                }
                quoteCommentRv.adapter = PostContentAdapter(mContext, topicId, null).apply {
                    comments = totalCommentData
                    type = PostContentAdapter.TYPE.QUOTE
                    this.data = JsonUtil.modelListA2B(data.reply_content, ContentViewBean::class.java, data.reply_content.size)
                }

                quoteLayout.post {
                    if (quoteLayout.height >= ScreenUtil.dip2px(mContext, 150f)) {
                        btnViewOriginComment.visibility = View.VISIBLE
                        btnViewOriginComment.setOnClickListener {
                            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            val bundle = Bundle().apply {
                                putInt(Constant.IntentKey.TOPIC_ID, topicId)
                                putSerializable(Constant.IntentKey.DATA_1, data)
                            }
                            if (mContext is FragmentActivity) {
                                ViewOriginCommentFragment
                                    .getInstance(bundle)
                                    .show((mContext as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                            }
                        }
                    } else {
                        btnViewOriginComment.visibility = View.GONE
                    }
                }
            } else {
                quoteLayout.visibility = View.GONE
                backUpQuoteLayout.visibility = View.VISIBLE
                backUpQuoteName.text = "${item.quote_user_name}•${item.quote_time}"
                backUpQuoteContent.text = item.quote_content_bare
            }
        } else {
            quoteLayout.visibility = View.GONE
            backUpQuoteLayout.visibility = View.GONE
        }
    }

    /**
     * 更新点赞按钮
     */
    private fun updateSupport(helper: BaseViewHolder, item: PostDetailBean.ListBean) {
        val support = helper.getView<TextView>(R.id.support_count)
        val supportIcon = helper.getView<ImageView>(R.id.image1)
        if (item.supportedCount != 0) {
            support.text = item.supportedCount.toString()
        } else {
            support.text = ""
            supportIcon.imageTintList = ColorStateList.valueOf(mContext.getColor(R.color.image_tint))
        }
        if (item.isSupported) {
            support.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
            supportIcon.imageTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
        } else {
            support.setTextColor(mContext.getColor(R.color.image_tint))
            supportIcon.imageTintList = ColorStateList.valueOf(mContext.getColor(R.color.image_tint))
        }
    }

    /**
     * 更新热评图标
     */
    private fun updateHotImg(helper: BaseViewHolder, item: PostDetailBean.ListBean) {
        val hotImg = helper.getView<ImageView>(R.id.hot_img)
        hotImg.visibility = if (item.isHotComment) View.VISIBLE else View.GONE
    }

    private fun updateAwardInfo(helper: BaseViewHolder, item: PostDetailBean.ListBean) {
        val awardInfo = helper.getView<TextView>(R.id.award_info)
        awardInfo.text = item.awardInfo?.replace(" ", "")
        awardInfo.visibility = if (item.awardInfo.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}