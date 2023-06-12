package com.scatl.uestcbbs.module.darkroom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDarkRoomBinding
import com.scatl.uestcbbs.entity.DarkRoomBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder

/**
 * Created by sca_tl at 2023/6/6 16:06
 */
class DarkRoomAdapter : PreloadAdapter<DarkRoomBean, ItemDarkRoomBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemDarkRoomBinding {
        return ItemDarkRoomBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemDarkRoomBinding>, position: Int, item: DarkRoomBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.userName.text = item.username
        holder.binding.actionTime.text = item.actionTime
        holder.binding.dateLine.text = item.dateline
        holder.binding.reason.text = item.username

        holder.binding.action.apply {
            text = item.action
            setTextColor(
                when(text) {
                    "禁止发言" -> { Color.parseColor("#CCAF12") }
                    "禁止访问" -> { Color.parseColor("#CC4347") }
                    else -> { Color.parseColor("#DDDDDD") }
                }
            )
        }
    }
}