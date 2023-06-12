package com.scatl.uestcbbs.module.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDianpingMsgBinding
import com.scatl.uestcbbs.entity.DianPingMsgBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 14:20
 */
class DianPingMsgAdapter: PreloadAdapter<DianPingMsgBean.BodyBean.DataBean, ItemDianpingMsgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemDianpingMsgBinding {
        return ItemDianpingMsgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemDianpingMsgBinding>, position: Int, item: DianPingMsgBean.BodyBean.DataBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.comment_user_name
        holder.binding.replyContent.text = "点评了你的帖子，点击查看"
        holder.binding.boardName.text = "来自板块:${item.board_name}"
        holder.binding.subjectTitle.text = item.topic_subject
        holder.binding.subjectContent.text = item.reply_content.trim()
        holder.binding.replyDate.text = TimeUtil.formatTime(item.replied_date, R.string.post_time1, context)
        holder.binding.userIcon.load(Constant.USER_AVATAR_URL + item.comment_user_id)
    }
}