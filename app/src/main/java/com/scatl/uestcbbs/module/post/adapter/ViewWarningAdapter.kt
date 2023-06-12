package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemViewWarningBinding
import com.scatl.uestcbbs.entity.ViewWarningItem
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/12 13:32
 */
class ViewWarningAdapter : PreloadAdapter<ViewWarningItem, ItemViewWarningBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemViewWarningBinding {
        return ItemViewWarningBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemViewWarningBinding>, position: Int, item: ViewWarningItem?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.name.text = item.name
        holder.binding.date.text = item.time
        holder.binding.avatar.load(item.avatar)
    }

}