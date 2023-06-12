package com.scatl.uestcbbs.module.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemReplyMeMsgBinding
import com.scatl.uestcbbs.entity.ReplyMeMsgBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 14:49
 */
class ReplyMeMsgAdapter: PreloadAdapter<ReplyMeMsgBean.BodyBean.DataBean, ItemReplyMeMsgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemReplyMeMsgBinding {
        return ItemReplyMeMsgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemReplyMeMsgBinding>, position: Int, item: ReplyMeMsgBean.BodyBean.DataBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.user_name
        holder.binding.boardName.text = "来自板块:${item.board_name}"
        holder.binding.replyDate.text = TimeUtil.formatTime(item.replied_date, R.string.post_time1, context)
        holder.binding.userIcon.load(item.icon)
        holder.binding.subjectTitle.apply {
            text = if (item.topic_subject.isNullOrEmpty()) {
                ""
            } else {
                item.topic_subject.replace("\r\n", "")
            }
            visibility = if (item.topic_subject.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        holder.binding.replyContent.apply {
            text = if (item.reply_content.isNullOrEmpty()) {
                ""
            } else {
                item.reply_content
                    .replace("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n", "\n")
                    .replace("\r\n", "")
            }
            visibility = if (item.reply_content.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        //显示未读标志
        holder.binding.newMsgImg.visibility =
            if (holder.layoutPosition < MessageManager.INSTANCE.replyUnreadCount) View.VISIBLE else View.GONE
    }
}