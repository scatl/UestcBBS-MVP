package com.scatl.uestcbbs.module.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemHistoryBinding
import com.scatl.uestcbbs.entity.HistoryBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 16:26
 */
class HistoryAdapter: PreloadAdapter<HistoryBean, ItemHistoryBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemHistoryBinding {
        return ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemHistoryBinding>, position: Int, item: HistoryBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.user_nick_name
        holder.binding.boardName.text = item.board_name
        holder.binding.title.text = item.title
        holder.binding.content.text = item.subject
        holder.binding.browseTime.text = TimeUtil.formatTime(item.browserTime.toString(), R.string.post_time1, context) + "浏览"
        holder.binding.postTime.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.post_time, context)
        holder.binding.avatar.load(item.userAvatar)
    }
}