package com.scatl.uestcbbs.module.post.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.PostDianPingBean
import com.scatl.uestcbbs.manager.BlackListManager
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.widget.span.CustomClickableSpan
import com.scatl.util.RegexUtil

/**
 * Created by sca_tl at 2023/4/12 13:32
 */
class DianPingAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<PostDianPingBean.List, BaseViewHolder>(layoutResId, onPreload) {

    fun addData(newData: List<PostDianPingBean.List>, reload: Boolean) {
        val filterData = newData.filter {
            !BlackListManager.INSTANCE.isBlacked(it.uid)
        }
        if (reload) {
            setNewData(filterData)
        } else {
            addData(filterData)
        }
    }

    override fun convert(helper: BaseViewHolder, item: PostDianPingBean.List) {
        super.convert(helper, item)
        helper
            .setText(R.id.name, item.userName)
            .setText(R.id.date, item.date)
            .addOnClickListener(R.id.avatar)
            .addOnClickListener(R.id.root_layout)

        val avatar = helper.getView<ImageView>(R.id.avatar)
        val comment = helper.getView<TextView>(R.id.comment)
        avatar.load(item.userAvatar)

        val result = RegexUtil.matchUrl(item.comment)
        if (result.isEmpty()) {
            comment.text = item.comment
        } else {
            val spannableString = SpannableStringBuilder(item.comment)
            try {
                result.forEach {
                    spannableString.setSpan(CustomClickableSpan(mContext, it.value), it.range.first, it.range.last, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            comment.text = spannableString
            comment.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}