package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemPostCommentBinding
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SupportedBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
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
class PostCommentAdapter : PreloadAdapter<PostDetailBean.ListBean, ItemPostCommentBinding>() {

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
            submitList(filterData)
        } else {
            addAll(filterData)
        }
    }

    override fun getViewBinding(parent: ViewGroup): ItemPostCommentBinding {
        return ItemPostCommentBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPostCommentBinding>, position: Int, item: PostDetailBean.ListBean?, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, item, payloads)
        if (item == null) {
            return
        }
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position, item)
        } else {
            if (payloads[0] is String) {
                if (UPDATE_SUPPORT == payloads[0]) {
                    item.isSupported = true
                    item.supportedCount++
                    item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(context)
                    val supportedBean = SupportedBean()
                    supportedBean.pid = item.reply_posts_id
                    supportedBean.save()
                    updateSupport(holder, item)
                    updateHotImg(holder, item)
                }
            } else if (payloads[0] is Bundle) {
                val key = (payloads[0] as Bundle).getString("key")
                if (key == UPDATE_AWARD_INFO) {
                    val awardInfo = (payloads[0] as Bundle).getString("info")
                    item.awardInfo = awardInfo
                    updateAwardInfo(holder, item)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPostCommentBinding>, position: Int, item: PostDetailBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding

        if (item.reply_id == 0 && "匿名" == item.reply_name) {
            binding.replyAvatar.load(R.drawable.ic_anonymous)
        } else {
            binding.replyAvatar.load(item.icon)
        }

        binding.replyName.text = item.reply_name
        binding.postTime.text = TimeUtil.formatTime(item.posts_date, R.string.post_time1, context)
        binding.authorTag.visibility = if (item.reply_id == authorId && item.reply_id != 0) View.VISIBLE else View.GONE
        binding.mobileSign.text = if (item.mobileSign.isNullOrEmpty()) "网页版" else item.mobileSign.replace("来自", "")

        binding.floor.text = if (item.position in 2..5) Constant.FLOOR[item.position - 2] else "#${item.position}"
        if (item.poststick == 1) {
            binding.floor.text = "置顶"
            binding.floor.setBackgroundResource(R.drawable.shape_post_detail_user_level_1)
        } else {
            binding.floor.setTextColor(ColorUtil.getAttrColor(context, R.attr.colorPrimary))
            binding.floor.background = null
        }

        if (!item.extraPanel.isNullOrEmpty()) {
            if ("support" == item.extraPanel[0].type) {
                item.supportedCount = item.extraPanel[0].extParams?.recommendAdd?:0
            }
        }
        item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(context)
        item.isSupported = null != LitePal.where("pid = " + item.reply_posts_id).findFirst(SupportedBean::class.java)

        updateSupport(holder, item)
        updateHotImg(holder, item)
        updateAwardInfo(holder, item)

        if (!item.userTitle.isNullOrEmpty()) {
            binding.replyLevel.visibility = View.VISIBLE
            val matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle)
            binding.replyLevel.backgroundTintList = ColorStateList.valueOf(ForumUtil.getLevelColor(context, item.userTitle))
            binding.replyLevel.text = if (matcher.find()) {
                if (matcher.group(2)?.contains("禁言") == true) { "禁言中" } else { matcher.group(2) }
            } else {
                item.userTitle
            }
        } else {
            binding.replyLevel.visibility = View.GONE
        }

        binding.contentRv.adapter = PostContentAdapter(context, topicId, null).apply {
            type = PostContentAdapter.TYPE.REPLY
            this.data = JsonUtil.modelListA2B(item.reply_content, ContentViewBean::class.java, item.reply_content.size)
            comments = totalCommentData
        }

        //有引用内容
        if (item.is_quote == 1) {
            val data = CommentUtil.findCommentByPid(totalCommentData, item.quote_pid)
            if (data != null) {
                binding.quoteLayout.visibility = View.VISIBLE
                binding.backUpQuoteLayout.visibility = View.GONE
                binding.quoteName.text = data.reply_name
                if (data.reply_id == 0 && "匿名" == data.reply_name) {
                    binding.quoteAvatar.load(R.drawable.ic_anonymous)
                } else {
                    binding.quoteAvatar.load(data.icon)
                }
                binding.originCommentRv.adapter = PostContentAdapter(context, topicId, null).apply {
                    comments = totalCommentData
                    type = PostContentAdapter.TYPE.QUOTE
                    this.data = JsonUtil.modelListA2B(data.reply_content, ContentViewBean::class.java, data.reply_content.size)
                }

                binding.quoteLayout.post {
                    if (binding.quoteLayout.height >= ScreenUtil.dip2px(context, 150f)) {
                        binding.btnViewFullQuote.visibility = View.VISIBLE
                        binding.btnViewFullQuote.setOnClickListener {
                            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            val bundle = Bundle().apply {
                                putInt(Constant.IntentKey.TOPIC_ID, topicId)
                                putSerializable(Constant.IntentKey.DATA_1, data)
                            }
                            if (context is FragmentActivity) {
                                ViewOriginCommentFragment
                                    .getInstance(bundle)
                                    .show((context as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                            }
                        }
                    } else {
                        binding.btnViewFullQuote.visibility = View.GONE
                    }
                }
            } else {
                binding.quoteLayout.visibility = View.GONE
                binding.backUpQuoteLayout.visibility = View.VISIBLE
                binding.backUpQuoteName.text = "${item.quote_user_name}•${item.quote_time}"
                binding.backUpQuoteContent.text = item.quote_content_bare
            }
        } else {
            binding.quoteLayout.visibility = View.GONE
            binding.backUpQuoteLayout.visibility = View.GONE
        }
    }

    /**
     * 更新点赞按钮
     */
    private fun updateSupport(holder: ViewBindingHolder<ItemPostCommentBinding>, item: PostDetailBean.ListBean) {
        if (item.supportedCount != 0) {
            holder.binding.supportCount.text = item.supportedCount.toString()
        } else {
            holder.binding.supportCount.text = ""
            holder.binding.supportIcon.imageTintList = ColorStateList.valueOf(context.getColor(R.color.image_tint))
        }
        if (item.isSupported) {
            holder.binding.supportCount.setTextColor(ColorUtil.getAttrColor(context, R.attr.colorPrimary))
            holder.binding.supportIcon.imageTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorPrimary))
        } else {
            holder.binding.supportCount.setTextColor(context.getColor(R.color.image_tint))
            holder.binding.supportIcon.imageTintList = ColorStateList.valueOf(context.getColor(R.color.image_tint))
        }
    }

    /**
     * 更新热评图标
     */
    private fun updateHotImg(holder: ViewBindingHolder<ItemPostCommentBinding>, item: PostDetailBean.ListBean) {
        holder.binding.hotImg.visibility = if (item.isHotComment) View.VISIBLE else View.GONE
    }

    private fun updateAwardInfo(holder: ViewBindingHolder<ItemPostCommentBinding>, item: PostDetailBean.ListBean) {
        holder.binding.awardInfo.text = item.awardInfo?.replace(" ", "")
        holder.binding.awardInfo.visibility = if (item.awardInfo.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}