package com.scatl.uestcbbs.module.collection.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemCollectionTagBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import kotlin.random.Random

/**
 * Created by sca_tl at 2023/5/4 19:09
 */
class CollectionTagAdapter : PreloadAdapter<String, ItemCollectionTagBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCollectionTagBinding {
        return ItemCollectionTagBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCollectionTagBinding>, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.text.apply {
            this.text = item
            this.setBackgroundResource(R.drawable.shape_collection_tag)
            this.backgroundTintList = ColorStateList.valueOf(Color.parseColor(Constant.TAG_COLOR[Random.nextInt(Constant.TAG_COLOR.size)]))
        }
    }

}