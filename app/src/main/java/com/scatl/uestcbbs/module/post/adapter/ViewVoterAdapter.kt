package com.scatl.uestcbbs.module.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemViewVoterBinding
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.ViewVoterBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/6/12 14:04
 */
class ViewVoterAdapter: PreloadAdapter<ViewVoterBean, ItemViewVoterBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemViewVoterBinding {
        return ItemViewVoterBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemViewVoterBinding>, position: Int, item: ViewVoterBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        holder.binding.name.text = item.name
        holder.binding.avatar.load(Constant.USER_AVATAR_URL + item.uid)
    }
}