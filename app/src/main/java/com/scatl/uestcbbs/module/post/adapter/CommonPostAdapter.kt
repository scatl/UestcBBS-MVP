package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.helper.BlackListManager
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.module.post.view.CommonPostFragment
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.widget.ninelayout.NineGridLayout


/**
 * Created by tanlei02 at 2023/4/25 17:23
 */
@SuppressLint("SetTextI18n")
class CommonPostAdapter(layoutResId: Int, val type: String = "", onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CommonPostBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: CommonPostBean.ListBean) {
        super.convert(helper, item)
        val contentLayout = helper.getView<View>(R.id.content_layout)
        val avatar = helper.getView<ImageView>(R.id.avatar)
        val userName = helper.getView<TextView>(R.id.user_name)
        val time = helper.getView<TextView>(R.id.time)
        val title = helper.getView<TextView>(R.id.title)
        val content = helper.getView<TextView>(R.id.content)
        val boardName = helper.getView<TextView>(R.id.board_name)
        val imageLayout = helper.getView<NineGridLayout>(R.id.image_layout)
        val pollLayout = helper.getView<View>(R.id.poll_layout)
        val supportCount = helper.getView<TextView>(R.id.support_count)
        val commentCount = helper.getView<TextView>(R.id.comment_count)
        val viewCount = helper.getView<TextView>(R.id.view_count)

        if (BlackListManager.INSTANCE.isBlacked(item.user_id)) {
            contentLayout.visibility = View.GONE
        } else {
            contentLayout.visibility = View.VISIBLE
        }

        helper
            .addOnClickListener(R.id.avatar)
            .addOnClickListener(R.id.board_name)
            .addOnClickListener(R.id.content_layout)

        userName.text = item.user_nick_name
        title.text = item.title
        boardName.text = item.board_name
        supportCount.text = " ${item.recommendAdd}"
        commentCount.text = " ${item.replies}"
        viewCount.text = " ${item.hits}"

        if (item.user_id == 0 || "匿名" == item.user_nick_name) {
            avatar.load(R.drawable.ic_anonymous)
        } else {
            avatar.load(item.userAvatar)
        }

        content.apply {
            text = item.subject
            visibility = if (item.subject.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        pollLayout.visibility = if (1 == item.vote) View.VISIBLE else View.GONE

        if (type == CommonPostFragment.TYPE_HOT_POST) {
            time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.post_time, mContext)
        } else {
            time.text = TimeUtil.formatTime(item.last_reply_date.toString(), R.string.reply_time, mContext)
        }

        setImages(item, imageLayout)
    }

    private fun setImages(item: CommonPostBean.ListBean, imageLayout: NineGridLayout) {
        if (item.imageList != null) {
            val iterator = item.imageList.iterator()
            while (iterator.hasNext()) {
                if (Constant.SPLIT_LINES.contains(iterator.next())) {
                    iterator.remove()
                }
            }
        }
        if (!item.imageList.isNullOrEmpty()) {
            imageLayout.visibility = View.VISIBLE
            imageLayout.setNineGridAdapter(NineImageAdapter(item.imageList))
        } else {
            imageLayout.visibility = View.GONE
        }
    }
}