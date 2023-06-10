package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemAttachmentBinding
import com.scatl.uestcbbs.entity.AttachmentBean
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * created by sca_tl at 2023/6/10 11:16
 */
class AttachmentAdapter: PreloadAdapter<AttachmentBean, ItemAttachmentBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemAttachmentBinding {
        return ItemAttachmentBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: AttachmentBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemAttachmentBinding

        binding.itemAttachmentFileName.text = item.fileName
    }
}