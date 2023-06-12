package com.scatl.uestcbbs.module.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemUserFriendBinding
import com.scatl.uestcbbs.entity.UserFriendBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 17:31
 */
class UserFriendAdapter: PreloadAdapter<UserFriendBean.ListBean, ItemUserFriendBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemUserFriendBinding {
        return ItemUserFriendBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemUserFriendBinding>, position: Int, item: UserFriendBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.name.text = item.name
        holder.binding.lastLogin.text = TimeUtil.formatTime(item.lastLogin, R.string.last_login_time, context)
        holder.binding.avatar.load(item.icon)
    }
}