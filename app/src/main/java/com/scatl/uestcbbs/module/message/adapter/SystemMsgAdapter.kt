package com.scatl.uestcbbs.module.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemSystemMsgBinding
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 15:01
 */
class SystemMsgAdapter: PreloadAdapter<SystemMsgBean.BodyBean.DataBean, ItemSystemMsgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemSystemMsgBinding {
        return ItemSystemMsgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemSystemMsgBinding>, position: Int, item: SystemMsgBean.BodyBean.DataBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.user_name
        holder.binding.content.text = item.note
        holder.binding.date.text = TimeUtil.formatTime(item.replied_date, R.string.post_time1, context)
        holder.binding.userIcon.load(item.icon)

        if (item.actions.isNullOrEmpty()) {
            holder.binding.actionBtn.visibility = View.GONE
        } else {
            holder.binding.actionBtn.visibility = View.VISIBLE
            holder.binding.actionBtn.text = item.actions[0].title
        }
    }
}