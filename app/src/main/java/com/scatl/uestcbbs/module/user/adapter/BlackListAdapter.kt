package com.scatl.uestcbbs.module.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemBlackListBinding
import com.scatl.uestcbbs.entity.BlackListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/25 15:13
 */
class BlackListAdapter : PreloadAdapter<BlackListBean, ItemBlackListBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemBlackListBinding {
        return ItemBlackListBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemBlackListBinding>, position: Int, item: BlackListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.avatar.load(item.avatar)
        holder.binding.name.text = item.userName
    }
}