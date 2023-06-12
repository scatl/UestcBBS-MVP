package com.scatl.uestcbbs.module.magic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemMagicShopBinding
import com.scatl.uestcbbs.entity.MagicShopBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 15:16
 */
class MagicShopAdapter: PreloadAdapter<MagicShopBean.ItemList, ItemMagicShopBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemMagicShopBinding {
        return ItemMagicShopBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemMagicShopBinding>, position: Int, item: MagicShopBean.ItemList?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.magicDsp.text = item.dsp
        holder.binding.magicName.text = item.name
        holder.binding.magicPrice.text = item.price
        holder.binding.magicIcon.load(item.icon)
    }
}