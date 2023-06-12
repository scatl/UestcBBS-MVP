package com.scatl.uestcbbs.module.post.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemPingFenBinding
import com.scatl.uestcbbs.entity.RateUserBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/20 16:45
 */
class PingFenAdapter : PreloadAdapter<RateUserBean, ItemPingFenBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemPingFenBinding {
        return ItemPingFenBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPingFenBinding>, position: Int, item: RateUserBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.name.text = item.userName
        holder.binding.time.text = item.time
        holder.binding.reason.text = item.reason
        holder.binding.credit.text = item.credit
        holder.binding.credit.setTextColor(
            if (item.credit.contains("水滴"))
            Color.parseColor("#108EE9")
            else Color.parseColor("#D3CC00")
        )

        holder.binding.avatar.load(Constant.USER_AVATAR_URL + item.uid)
    }
}