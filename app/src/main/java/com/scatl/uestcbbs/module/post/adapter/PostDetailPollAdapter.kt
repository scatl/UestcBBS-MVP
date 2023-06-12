package com.scatl.uestcbbs.module.post.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.scatl.uestcbbs.databinding.ItemPostDetailPollBinding
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.R

/**
 * Created by sca_tl at 2023/6/12 11:38
 */
class PostDetailPollAdapter: PreloadAdapter<PostDetailBean.TopicBean.PollInfoBean.PollItemListBean, ItemPostDetailPollBinding>() {

    var totalCount = 0
    var pollStatus = 0
    val optionIds: MutableList<Int> = ArrayList()

    override fun getViewBinding(parent: ViewGroup): ItemPostDetailPollBinding {
        return ItemPostDetailPollBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPostDetailPollBinding>, position: Int, item: PostDetailBean.TopicBean.PollInfoBean.PollItemListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (!optionIds.contains(item.poll_item_id)) {
                    optionIds.add(item.poll_item_id)
                }
            } else {
                if (optionIds.contains(item.poll_item_id)) {
                    optionIds.remove(Integer.valueOf(item.poll_item_id))
                }
            }
        }

        holder.binding.checkbox.isEnabled = pollStatus == 2
        holder.binding.checkbox.text = context.getString(R.string.vote_item_voted_num, item.name, item.total_num, item.percent)

        holder.binding.progress.max = totalCount * 100
        holder.binding.progress.postDelayed({
            ValueAnimator
                .ofInt(0, item.total_num * 100)
                .setDuration(500)
                .apply {
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animation ->
                        holder.binding.progress.progress = animation.animatedValue as Int
                    }
                    start()
                }
        }, 500)
    }
}