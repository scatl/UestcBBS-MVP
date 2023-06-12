package com.scatl.uestcbbs.module.collection.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemSameOwnerCollectionBinding
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.util.ColorUtil

/**
 * Created by sca_tl at 2023/5/4 20:17
 */
class CollectionSameOwnerAdapter : PreloadAdapter<CollectionDetailBean.SameOwnerCollection, ItemSameOwnerCollectionBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemSameOwnerCollectionBinding {
        return ItemSameOwnerCollectionBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemSameOwnerCollectionBinding>, position: Int, item: CollectionDetailBean.SameOwnerCollection?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.text.apply {
            this.text = item.name
            this.setBackgroundResource(R.drawable.shape_collection_tag)
            this.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorSurfaceVariant))
        }
    }
}