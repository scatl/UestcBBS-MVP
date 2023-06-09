package com.scatl.uestcbbs.module.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemUserSpaceMedalBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 17:35
 */
class UserMedalAdapter: PreloadAdapter<String, ItemUserSpaceMedalBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemUserSpaceMedalBinding {
        return ItemUserSpaceMedalBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemUserSpaceMedalBinding

        binding.medalImg.load(item)
    }
}