package com.scatl.uestcbbs.module.magic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemMineMagicBinding
import com.scatl.uestcbbs.entity.MineMagicBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 15:20
 */
class MineMagicAdapter: PreloadAdapter<MineMagicBean.ItemList, ItemMineMagicBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemMineMagicBinding {
        return ItemMineMagicBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemMineMagicBinding>, position: Int, item: MineMagicBean.ItemList?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.magicDsp.text = item.dsp
        holder.binding.magicName.text = item.name
        holder.binding.magicWeight.text = "数量：${item.totalCount}  重量：${item.totalWeight}"
        holder.binding.magicIcon.load(item.icon)
        holder.binding.magicUseBtn.visibility = if (item.showUseBtn) View.VISIBLE else View.GONE
    }
}