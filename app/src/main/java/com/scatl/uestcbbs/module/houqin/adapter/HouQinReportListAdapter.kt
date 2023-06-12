package com.scatl.uestcbbs.module.houqin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemHouqinReportListBinding
import com.scatl.uestcbbs.entity.HouQinReportListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder

/**
 * Created by sca_tl at 2023/6/12 15:27
 */
class HouQinReportListAdapter: PreloadAdapter<HouQinReportListBean.TopicBean, ItemHouqinReportListBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemHouqinReportListBinding {
        return ItemHouqinReportListBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemHouqinReportListBinding>, position: Int, item: HouQinReportListBean.TopicBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.username.text = "发帖人：" + item.account
        holder.binding.title.text = item.title
        holder.binding.appliedTime.text = "受理时间：" + item.topDate
        holder.binding.updatetime.text = "更新时间：" + item.replyDate
        holder.binding.viewCount.text = "浏览/回复：" + item.readOrReply
        holder.binding.repliedPic.visibility = if ("已回复" == item.state) View.VISIBLE else View.GONE
    }
}