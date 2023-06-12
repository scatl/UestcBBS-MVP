package com.scatl.uestcbbs.module.home.adapter.latestpost

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.HomeItemNoticeViewBinding
import com.scatl.uestcbbs.entity.NoticeBean
import com.scatl.uestcbbs.helper.BaseOneItemAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder

/**
 * Created by sca_tl at 2023/6/12 16:21
 */
class NoticeAdapter: BaseOneItemAdapter<NoticeBean, HomeItemNoticeViewBinding>() {

    override fun getViewBinding(parent: ViewGroup): HomeItemNoticeViewBinding {
        return HomeItemNoticeViewBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<HomeItemNoticeViewBinding>, item: NoticeBean?) {
        super.onBindViewHolder(holder, item)
        if (item == null) {
            return
        }

        if (item.isValid) {
            holder.binding.contentLayout.visibility = View.VISIBLE
            holder.binding.content.apply {
                text = item.content
                setTextColor(Color.parseColor(item.color))
            }
        } else {
            holder.binding.contentLayout.visibility = View.GONE
        }
    }
}