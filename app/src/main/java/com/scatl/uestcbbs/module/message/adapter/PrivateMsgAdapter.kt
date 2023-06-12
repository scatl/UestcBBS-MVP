package com.scatl.uestcbbs.module.message.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemPrivateMsgBinding
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 14:40
 */
class PrivateMsgAdapter: PreloadAdapter<PrivateMsgBean.BodyBean.ListBean, ItemPrivateMsgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemPrivateMsgBinding {
        return ItemPrivateMsgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPrivateMsgBinding>, position: Int, item: PrivateMsgBean.BodyBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.toUserName
        holder.binding.content.text = if (TextUtils.isEmpty(item.lastSummary)) "[图片]" else item.lastSummary
        holder.binding.time.text = TimeUtil.formatTime(item.lastDateline, R.string.post_time1, context)
        holder.binding.userIcon.load(item.toUserAvatar)
        holder.binding.unreadImg.visibility = if (item.isNew == 1) View.VISIBLE else View.GONE
    }
}