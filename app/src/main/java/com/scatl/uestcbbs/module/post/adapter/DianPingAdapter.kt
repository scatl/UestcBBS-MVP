package com.scatl.uestcbbs.module.post.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDianpingBinding
import com.scatl.uestcbbs.entity.PostDianPingBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.widget.span.CustomClickableSpan
import com.scatl.util.RegexUtil

/**
 * Created by sca_tl at 2023/4/12 13:32
 */
class DianPingAdapter : PreloadAdapter<PostDianPingBean.List, ItemDianpingBinding>() {

    fun addData(newData: List<PostDianPingBean.List>, reload: Boolean) {
        val filterData = newData.filter {
            !BlackListManager.INSTANCE.isBlacked(it.uid)
        }
        if (reload) {
            submitList(filterData)
        } else {
            addAll(filterData)
        }
    }

    override fun getViewBinding(parent: ViewGroup): ItemDianpingBinding {
        return ItemDianpingBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemDianpingBinding>, position: Int, item: PostDianPingBean.List?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        holder.binding.name.text = item.userName
        holder.binding.date.text = item.date
        holder.binding.avatar.load(item.userAvatar)

        val result = RegexUtil.matchUrl(item.comment)
        if (result.isEmpty()) {
            holder.binding.comment.text = item.comment
        } else {
            val spannableString = SpannableStringBuilder(item.comment)
            try {
                result.forEach {
                    spannableString.setSpan(CustomClickableSpan(context, it.value), it.range.first, it.range.last, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.binding.comment.text = spannableString
            holder.binding.comment.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}