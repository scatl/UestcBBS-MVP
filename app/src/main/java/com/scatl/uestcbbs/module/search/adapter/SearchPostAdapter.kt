package com.scatl.uestcbbs.module.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemSimplePostBinding
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load
import com.scatl.util.ColorUtil

/**
 * Created by sca_tl at 2023/4/4 10:31
 */
class SearchPostAdapter : PreloadAdapter<SearchPostBean.ListBean, ItemSimplePostBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemSimplePostBinding {
        return ItemSimplePostBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    fun addSearchPostData(data: List<SearchPostBean.ListBean>, refresh: Boolean) {
        val newList: MutableList<SearchPostBean.ListBean> = ArrayList()
        for (i in data.indices) {
            if (data[i].user_nick_name.isNullOrEmpty()) {
                data[i].user_nick_name = Constant.ANONYMOUS_NAME
                data[i].avatar = Constant.DEFAULT_AVATAR
                data[i].user_id = 0
            } else {
                data[i].avatar = Constant.USER_AVATAR_URL + data[i].user_id
            }
            newList.add(data[i])
        }

        if (refresh) {
            submitList(newList)
        } else {
            addAll(newList)
        }
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemSimplePostBinding>, position: Int, item: SearchPostBean.ListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding

        binding.itemSimplePostTitle.text = item.title
        binding.itemSimplePostContent.text = item.subject
        binding.itemSimplePostUserName.text = item.user_nick_name
        binding.itemSimplePostCommentsCount.text = "  ".plus(item.replies)
        binding.itemSimplePostViewCount.text = "  ".plus(item.hits)
        binding.itemSimplePostTime.text = TimeUtil.formatTime(item.last_reply_date, R.string.reply_time, context)

        binding.itemSimplePostCardView.setCardBackgroundColor(ColorUtil.getAttrColor(context, R.attr.colorOnSurfaceInverse))
        binding.itemSimplePostBoardName.visibility = View.GONE
        binding.itemSimplePostPollRl.visibility = if (item.vote == 1) View.VISIBLE else View.GONE

        if (item.user_id == 0 && "匿名" == item.user_nick_name) {
            binding.itemSimplePostUserAvatar.load(R.drawable.ic_anonymous)
        } else {
            binding.itemSimplePostUserAvatar.load(item.avatar)
        }
    }
}