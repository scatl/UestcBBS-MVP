package com.scatl.uestcbbs.module.darkroom.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDarkRoomBinding
import com.scatl.uestcbbs.entity.DarkRoomBean
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * Created by sca_tl at 2023/6/6 16:06
 */
class DarkRoomAdapter : PreloadAdapter<DarkRoomBean, ItemDarkRoomBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemDarkRoomBinding {
        return ItemDarkRoomBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: DarkRoomBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemDarkRoomBinding

        binding.userName.text = item.username
        binding.actionTime.text = item.actionTime
        binding.dateLine.text = item.dateline
        binding.reason.text = item.username

        binding.action.apply {
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