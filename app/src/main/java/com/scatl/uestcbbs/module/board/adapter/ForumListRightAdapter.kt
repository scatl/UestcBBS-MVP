package com.scatl.uestcbbs.module.board.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemForumListRightBinding
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.SharePrefUtil

/**
 * Created by sca_tl at 2023/6/9 15:34
 */
class ForumListRightAdapter: PreloadAdapter<ForumListBean.ListBean, ItemForumListRightBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemForumListRightBinding {
        return ItemForumListRightBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemForumListRightBinding>, position: Int, item: ForumListBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.name.text = item.board_category_name
        holder.binding.gridview.apply {
            numColumns = SharePrefUtil.getBoardListColumns(context)
            adapter = ForumListGridViewAdapter(context, items[holder.layoutPosition].board_list)
            requestFocus()
        }
    }
}