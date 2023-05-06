package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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
import com.scatl.uestcbbs.widget.span.CenterImageSpan
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.ninelayout.NineGridLayout


/**
 * Created by sca_tl at 2023/4/25 17:23
 */
@SuppressLint("SetTextI18n")
class CommonPostAdapter(layoutResId: Int, val type: String = "", onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CommonPostBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    fun addData(newData: MutableCollection<out CommonPostBean.ListBean>, reload: Boolean) {
        val realData = newData.filter {
            !data.contains(it) || !BlackListManager.INSTANCE.isBlacked(it.user_id)
        }
        if (reload) {
            setNewData(realData)
        } else {
            addData(realData)
        }
    }

    override fun convert(helper: BaseViewHolder, item: CommonPostBean.ListBean) {
        super.convert(helper, item)
        val avatar = helper.getView<ImageView>(R.id.avatar)
        val userName = helper.getView<TextView>(R.id.user_name)
        val time = helper.getView<TextView>(R.id.time)
        val title = helper.getView<TextView>(R.id.title)
        val content = helper.getView<TextView>(R.id.content)
        val boardName = helper.getView<TextView>(R.id.board_name)
        val imageLayout = helper.getView<NineGridLayout>(R.id.image_layout)
        val supportCount = helper.getView<TextView>(R.id.support_count)
        val commentCount = helper.getView<TextView>(R.id.comment_count)
        val viewCount = helper.getView<TextView>(R.id.view_count)

        helper
            .addOnClickListener(R.id.avatar)
            .addOnClickListener(R.id.board_name)
            .addOnClickListener(R.id.content_layout)

        userName.text = item.user_nick_name
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

        if (item.vote == 1) {
            val spannableStringBuilder = SpannableStringBuilder("I" + item.title)
            val drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_vote)
            if (drawable is VectorDrawable) {
                drawable.setTint(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
                val radio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                val rect = Rect(0, 0, (title.textSize * radio * 1.1f).toInt(), (title.textSize * 1.1f).toInt())
                drawable.bounds = rect
                val imageSpan = CenterImageSpan(drawable).apply {
                    rightPadding = ScreenUtil.dip2px(mContext, 2f)
                }
                spannableStringBuilder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            title.text = spannableStringBuilder
        } else {
            title.text = item.title
        }

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