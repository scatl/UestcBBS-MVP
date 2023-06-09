package com.scatl.uestcbbs.module.board.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemForumListLeftBinding
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.util.ColorUtil.getAttrColor

/**
 * Created by sca_tl at 2023/6/9 15:20
 */
class ForumListLeftAdapter: PreloadAdapter<ForumListBean.ListBean, ItemForumListLeftBinding>() {

    var selected = 0
        set(value) {
            field = value
            notifyItemChanged(selected)
        }

    override fun getViewBinding(parent: ViewGroup): ItemForumListLeftBinding {
        return ItemForumListLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: ForumListBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemForumListLeftBinding

        binding.text.text = item.board_category_name
        if (holder.layoutPosition == selected) {
            binding.text.textSize = 18f
            binding.text.setTextColor(getAttrColor(context, R.attr.colorPrimary))
        } else {
            binding.text.textSize = 15f
            binding.text.setTextColor(getAttrColor(context, R.attr.colorOnSurfaceVariant))
        }
    }
}