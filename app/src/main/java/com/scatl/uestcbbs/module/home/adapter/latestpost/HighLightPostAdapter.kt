package com.scatl.uestcbbs.module.home.adapter.latestpost

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.HomeItemTopTopicViewBinding
import com.scatl.uestcbbs.entity.HighLightPostBean
import com.scatl.uestcbbs.helper.BaseOneItemAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.isNullOrEmpty

/**
 * Created by sca_tl at 2023/6/12 16:25
 */
class HighLightPostAdapter: BaseOneItemAdapter<HighLightPostBean, HomeItemTopTopicViewBinding>() {

    companion object {
        const val PAY_LOAD_CHANGE_VISIBILITY = "PAY_LOAD_CHANGE_VISIBILITY"
    }

    override fun getViewBinding(parent: ViewGroup): HomeItemTopTopicViewBinding {
        return HomeItemTopTopicViewBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<HomeItemTopTopicViewBinding>, item: HighLightPostBean?, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            if (payloads[0] is String) {
                if (PAY_LOAD_CHANGE_VISIBILITY == payloads[0]) {
                    changeVisibility(holder)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<HomeItemTopTopicViewBinding>, item: HighLightPostBean?) {
        super.onBindViewHolder(holder, item)
        if (item == null) {
            return
        }

        if (item.mData.isNullOrEmpty() || SharePrefUtil.isCloseTopStickPost(context)) {
            holder.binding.contentLayout.visibility = View.GONE
        } else {
            val titles = mutableListOf<String>()
            item.mData.forEach {
                titles.add(it.title)
            }
            holder.binding.marqueeView.startWithList(titles)
            holder.binding.marqueeView.setOnItemClickListener { position, textView ->
                val intent = Intent().apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, item.mData[position].tid)
                }
                context.startActivity(intent)
            }
        }
    }

    private fun changeVisibility(holder: ViewBindingHolder<HomeItemTopTopicViewBinding>) {
        holder.binding.contentLayout.visibility = (if (holder.binding.contentLayout.visibility == View.GONE) View.VISIBLE else View.GONE)
    }
}