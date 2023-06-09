package com.scatl.uestcbbs.module.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemOnlineUserBinding
import com.scatl.uestcbbs.entity.OnLineUserBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 16:38
 */
class OnLineUserAdapter: PreloadAdapter<OnLineUserBean.UserBean, ItemOnlineUserBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemOnlineUserBinding {
        return ItemOnlineUserBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: OnLineUserBean.UserBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemOnlineUserBinding

        binding.userName.text = item.userName
        binding.activeTime.text = item.time
        binding.avatar.load(item.userAvatar)
    }
}