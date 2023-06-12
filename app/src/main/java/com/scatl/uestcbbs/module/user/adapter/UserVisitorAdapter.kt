package com.scatl.uestcbbs.module.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemUserVisitorBinding
import com.scatl.uestcbbs.entity.VisitorsBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 17:39
 */
class UserVisitorAdapter: PreloadAdapter<VisitorsBean, ItemUserVisitorBinding>() {

    var mineId = 0

    override fun getViewBinding(parent: ViewGroup): ItemUserVisitorBinding {
        return ItemUserVisitorBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemUserVisitorBinding>, position: Int, item: VisitorsBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.avatar.load(item.visitorAvatar)
        holder.binding.time.text = item.visitedTime
        holder.binding.name.text = item.visitedTime
        holder.binding.deleteBtn.visibility = if (mineId == item.visitorUid) View.VISIBLE else View.GONE
    }
}