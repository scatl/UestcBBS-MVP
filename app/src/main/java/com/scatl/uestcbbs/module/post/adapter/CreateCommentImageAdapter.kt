package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemCreateCommentImageBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/20 16:12
 */
class CreateCommentImageAdapter : PreloadAdapter<String, ItemCreateCommentImageBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCreateCommentImageBinding {
        return ItemCreateCommentImageBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCreateCommentImageBinding>, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.image.load(item)
    }
}