package com.scatl.uestcbbs.module.collection.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/5/6 17:18
 */
class CollectionPostAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CollectionDetailBean.PostListBean, BaseViewHolder>(layoutResId, onPreload) {

    fun addData(newData: MutableCollection<out CollectionDetailBean.PostListBean>, reload: Boolean) {
        val realData = newData.filter {
            !BlackListManager.INSTANCE.isBlacked(it.authorId)
        }
        if (reload) {
            setNewData(realData)
        } else {
            addData(realData)
        }
    }

    override fun convert(helper: BaseViewHolder, item: CollectionDetailBean.PostListBean) {
        super.convert(helper, item)
        helper.addOnClickListener(R.id.avatar)
        val avatar = helper.getView<ImageView>(R.id.avatar)
        val userName = helper.getView<TextView>(R.id.user_name)
        val time = helper.getView<TextView>(R.id.time)
        val title = helper.getView<TextView>(R.id.title)
        val count = helper.getView<TextView>(R.id.count)

        time.text = item.postDate
        title.text = item.topicTitle
        count.text = "评论：${item.commentCount}  浏览：${item.viewCount}"

        if (item.authorId == 0 && item.authorName.isNullOrEmpty()) {
            avatar.load(Constant.DEFAULT_AVATAR)
            userName.text = "匿名"
        } else {
            avatar.load(item.authorAvatar)
            userName.text = item.authorName
        }
    }
}