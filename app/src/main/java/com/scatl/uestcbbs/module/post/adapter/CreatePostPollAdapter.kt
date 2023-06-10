package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemCreatePostPollBinding
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * created by sca_tl at 2023/6/10 11:19
 */
class CreatePostPollAdapter: PreloadAdapter<String, ItemCreatePostPollBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCreatePostPollBinding {
        return ItemCreatePostPollBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemCreatePostPollBinding
        binding.itemCreatePostPollTextview.text = (holder.layoutPosition + 1).toString() + ". " + item
    }
}