package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemCommonPostBinding
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.module.post.view.CommonPostFragment
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.ninelayout.NineGridLayout
import com.scatl.widget.sapn.CenterImageSpan

/**
 * Created by sca_tl at 2023/4/25 17:23
 */
@SuppressLint("SetTextI18n")
class CommonPostAdapter(val type: String = "", onPreload: (() -> Unit)? = null)
    : PreloadAdapter<CommonPostBean.ListBean, ItemCommonPostBinding>(onPreload) {

    override fun getViewBinding(parent: ViewGroup): ItemCommonPostBinding {
        return ItemCommonPostBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    fun addData(newData: List<CommonPostBean.ListBean>, reload: Boolean) {
        val filterData = newData.filter {
            (if (reload) true else !items.contains(it)) && !BlackListManager.INSTANCE.isBlacked(it.user_id)
        }
        if (reload) {
            submitList(filterData)
        } else {
            addAll(filterData)
        }
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCommonPostBinding>, position: Int, item: CommonPostBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding

        val hideContent: Boolean? = item.subject?.startsWith("防偷窥")

        binding.userName.text = item.user_nick_name
        binding.boardName.text = item.board_name
        binding.supportCount.text = " ${item.recommendAdd}"
        binding.commentCount.text = " ${item.replies}"
        binding.viewCount.text = " ${item.hits}"

        if (item.user_id == 0 || "匿名" == item.user_nick_name) {
            binding.avatar.load(R.drawable.ic_anonymous)
        } else {
            binding.avatar.load(item.userAvatar)
        }

        binding.content.apply {
            text = item.subject
            visibility = if (item.subject.isNullOrEmpty() || hideContent == true) View.GONE else View.VISIBLE
        }

        if (item.vote == 1) {
            val spannableStringBuilder = SpannableStringBuilder("I" + item.title)
            val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_vote)
            if (drawable is VectorDrawable) {
                drawable.setTint(ColorUtil.getAttrColor(context, R.attr.colorPrimary))
                val radio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                val rect = Rect(0, 0, (binding.title.textSize * radio * 1.1f).toInt(), (binding.title.textSize * 1.1f).toInt())
                drawable.bounds = rect
                val imageSpan = CenterImageSpan(drawable).apply {
                    rightPadding = ScreenUtil.dip2px(context, 2f)
                }
                spannableStringBuilder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            binding.title.text = spannableStringBuilder
        } else {
            binding.title.text = item.title
        }

        if (type == CommonPostFragment.TYPE_HOT_POST) {
            binding.time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.post_time, context)
        } else {
            binding.time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.reply_time, context)
        }

        setImages(item, binding.imageLayout, hideContent)
    }

    private fun setImages(item: CommonPostBean.ListBean, imageLayout: NineGridLayout, hideContent: Boolean?) {
        if (item.imageList != null) {
            val iterator = item.imageList.iterator()
            while (iterator.hasNext()) {
                if (Constant.SPLIT_LINES.contains(iterator.next())) {
                    iterator.remove()
                }
            }
        }
        if (!item.imageList.isNullOrEmpty() && hideContent == false) {
            imageLayout.visibility = View.VISIBLE
            imageLayout.setNineGridAdapter(NineImageAdapter(item.imageList))
        } else {
            imageLayout.visibility = View.GONE
        }
    }
}