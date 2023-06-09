package com.scatl.uestcbbs.module.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemHistoryBinding
import com.scatl.uestcbbs.entity.HistoryBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 16:26
 */
class HistoryAdapter: PreloadAdapter<HistoryBean, ItemHistoryBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemHistoryBinding {
        return ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: HistoryBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemHistoryBinding

        binding.userName.text = item.user_nick_name
        binding.boardName.text = item.board_name
        binding.title.text = item.title
        binding.content.text = item.subject
        binding.browseTime.text = TimeUtil.formatTime(item.browserTime.toString(), R.string.post_time1, context) + "浏览"
        binding.postTime.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.post_time, context)
        binding.avatar.load(item.userAvatar)
    }
}