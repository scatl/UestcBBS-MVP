package com.scatl.uestcbbs.module.collection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemCollectionPostBinding
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/5/6 17:18
 */
class CollectionPostAdapter: PreloadAdapter<CollectionDetailBean.PostListBean, ItemCollectionPostBinding>() {

    fun addData(newData: MutableCollection<out CollectionDetailBean.PostListBean>, reload: Boolean) {
        val realData = newData.filter {
            !BlackListManager.INSTANCE.isBlacked(it.authorId)
        }
        if (reload) {
            submitList(realData)
        } else {
            addAll(realData)
        }
    }

    override fun getViewBinding(parent: ViewGroup): ItemCollectionPostBinding {
        return ItemCollectionPostBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemCollectionPostBinding>, position: Int, item: CollectionDetailBean.PostListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.time.text = item.postDate
        holder.binding.title.text = item.topicTitle
        holder.binding.count.text = "评论：${item.commentCount}  浏览：${item.viewCount}"

        if (item.authorId == 0 && item.authorName.isNullOrEmpty()) {
            holder.binding.avatar.load(Constant.DEFAULT_AVATAR)
            holder.binding.userName.text = "匿名"
        } else {
            holder.binding.avatar.load(item.authorAvatar)
            holder.binding.userName.text = item.authorName
        }
    }
}