package com.scatl.uestcbbs.module.post.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSONObject
import com.scatl.uestcbbs.databinding.ItemPostDraftBinding
import com.scatl.uestcbbs.entity.PostDraftBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.widget.ContentEditor

/**
 * Created by sca_tl at 2023/6/12 13:58
 */
class PostDraftAdapter: PreloadAdapter<PostDraftBean, ItemPostDraftBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemPostDraftBinding {
        return ItemPostDraftBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemPostDraftBinding>, position: Int, item: PostDraftBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        var content_summary: String? = ""
        var image_summary: String? = ""
        val jsonArray = JSONObject.parseArray(item.content)
        if (jsonArray != null && jsonArray.size > 0) {
            var content_found = false
            var image_found = false
            for (i in jsonArray.indices) {
                val type = jsonArray.getJSONObject(i).getIntValue("content_type")
                val content = jsonArray.getJSONObject(i).getString("content")
                if (!content_found && type == ContentEditor.CONTENT_TYPE_TEXT && !TextUtils.isEmpty(content)) {
                    content_found = true
                    content_summary = content
                }
                if (!image_found && type == ContentEditor.CONTENT_TYPE_IMAGE) {
                    image_found = true
                    image_summary = content
                }
                if (content_found && image_found) break
            }
        }

        holder.binding.title.text = item.title
        holder.binding.content.text = content_summary

        if (TextUtils.isEmpty(image_summary)) {
            holder.binding.img.visibility = View.GONE
        } else {
            holder.binding.img.visibility = View.VISIBLE
            holder.binding.img.load(image_summary)
        }
    }
}