package com.scatl.uestcbbs.module.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemAtUserListBinding
import com.scatl.uestcbbs.entity.AtUserListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/9 17:26
 */
class AtUserListAdapter: PreloadAdapter<AtUserListBean.ListBean, ItemAtUserListBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemAtUserListBinding {
        return ItemAtUserListBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemAtUserListBinding>, position: Int, item: AtUserListBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.name.text = item.name
        holder.binding.avatar.load(Constant.USER_AVATAR_URL.plus(item.uid))
    }
}