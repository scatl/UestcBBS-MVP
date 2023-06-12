package com.scatl.uestcbbs.module.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemAtMeMsgBinding
import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 14:14
 */
class AtMeMsgAdapter: PreloadAdapter<AtMsgBean.BodyBean.DataBean, ItemAtMeMsgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemAtMeMsgBinding {
        return ItemAtMeMsgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemAtMeMsgBinding>, position: Int, item: AtMsgBean.BodyBean.DataBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.user_name
        holder.binding.replyContent.text = item.reply_content.replace("\r\n".toRegex(), "")
        holder.binding.boardName.text = "来自板块:${item.board_name}"
        holder.binding.subjectTitle.text = item.topic_subject
        holder.binding.subjectContent.text = item.topic_content.trim()
        holder.binding.replyDate.text = TimeUtil.formatTime(item.replied_date, R.string.post_time1, context)
        holder.binding.userIcon.load(item.icon)
    }
}