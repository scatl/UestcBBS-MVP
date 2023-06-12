package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemPostDetailCollectionBinding
import com.scatl.uestcbbs.entity.PostWebBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder

/**
 * Created by sca_tl at 2023/6/12 11:35
 */
class PostCollectionAdapter: PreloadAdapter<PostWebBean.Collection, ItemPostDetailCollectionBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemPostDetailCollectionBinding {
        return ItemPostDetailCollectionBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPostDetailCollectionBinding>, position: Int, item: PostWebBean.Collection?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        holder.binding.name.text = item.name
        holder.binding.subscribeCount.text = item.subscribeCount
    }
}