package com.scatl.uestcbbs.module.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemSearchUserBinding
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/4 10:10
 */
class SearchUserAdapter : PreloadAdapter<SearchUserBean.BodyBean.ListBean, ItemSearchUserBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemSearchUserBinding {
        return ItemSearchUserBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemSearchUserBinding>, position: Int, item: SearchUserBean.BodyBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.searchUserName.text = item.name
        holder.binding.searchUserLastLogin.text = TimeUtil.formatTime(item.dateline, R.string.last_login_time, context)
        holder.binding.searchUserIcon.load(item.icon)
    }

}