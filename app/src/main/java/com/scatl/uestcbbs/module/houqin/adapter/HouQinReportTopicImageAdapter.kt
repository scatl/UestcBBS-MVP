package com.scatl.uestcbbs.module.houqin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemCreateCommentImageBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 15:31
 */
class HouQinReportTopicImageAdapter: PreloadAdapter<String, ItemCreateCommentImageBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCreateCommentImageBinding {
        return ItemCreateCommentImageBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCreateCommentImageBinding>, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        holder.binding.image.load(item)
        holder.binding.deleteBtn.visibility = View.GONE
    }
}