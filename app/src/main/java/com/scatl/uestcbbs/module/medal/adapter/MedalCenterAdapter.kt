package com.scatl.uestcbbs.module.medal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemMedalCenterBinding
import com.scatl.uestcbbs.entity.MedalBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 15:07
 */
class MedalCenterAdapter: PreloadAdapter<MedalBean.MedalCenterBean, ItemMedalCenterBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemMedalCenterBinding {
        return ItemMedalCenterBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemMedalCenterBinding>, position: Int, item: MedalBean.MedalCenterBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.medalName.text = item.medalName
        holder.binding.medalDsp.text = item.medalDsp
        holder.binding.medalIcon.load(item.medalIcon)

        if (item.buyDsp.isNullOrEmpty()) {
            holder.binding.getMedalBtn.visibility = View.GONE
        } else {
            holder.binding.getMedalBtn.visibility = View.VISIBLE
            holder.binding.getMedalBtn.text = item.buyDsp
        }
    }
}