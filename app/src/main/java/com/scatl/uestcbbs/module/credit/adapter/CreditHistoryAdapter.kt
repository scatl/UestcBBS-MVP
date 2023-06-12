package com.scatl.uestcbbs.module.credit.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemCreditHistoryBinding
import com.scatl.uestcbbs.entity.MineCreditBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.util.ColorUtil

/**
 * Created by sca_tl at 2023/4/12 14:44
 */
class CreditHistoryAdapter : PreloadAdapter<MineCreditBean.CreditHistoryBean, ItemCreditHistoryBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCreditHistoryBinding {
        return ItemCreditHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCreditHistoryBinding>, position: Int, item: MineCreditBean.CreditHistoryBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.rootLayout.background.alpha = (255 * 0.4).toInt()
        holder.binding.action.text = "操作：".plus(item.action)
        holder.binding.detail.text = item.detail
        holder.binding.time.text = "时间：".plus(item.time)

        holder.binding.change.text = "变更："
        val spannableString = SpannableString(item.change).apply {
            setSpan(
                AbsoluteSizeSpan((holder.binding.change.textSize + 12).toInt(), false), 0, item.change.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        holder.binding.change.append(spannableString)
        holder.binding.change.setTextColor(
            if (item.increase) ContextCompat.getColor(context, R.color.forum_color_1)
            else ColorUtil.getAttrColor(context, R.attr.colorOutline)
        )
    }
}