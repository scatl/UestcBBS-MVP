package com.scatl.uestcbbs.module.update.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemUpdateImgBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 17:48
 */
class UpdateImgAdapter: PreloadAdapter<String, ItemUpdateImgBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemUpdateImgBinding {
        return ItemUpdateImgBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemUpdateImgBinding

        binding.image.load(item)
    }
}