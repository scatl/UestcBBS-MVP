package com.scatl.uestcbbs.module.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemOpenSourceBinding
import com.scatl.uestcbbs.entity.OpenSourceBean
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * Created by sca_tl at 2023/6/9 17:15
 */
class OpenSourceAdapter: PreloadAdapter<OpenSourceBean, ItemOpenSourceBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemOpenSourceBinding {
        return ItemOpenSourceBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: OpenSourceBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemOpenSourceBinding

        binding.projectName.text = item.name
        binding.authorName.text = item.author
        binding.dsp.text = item.description
    }
}