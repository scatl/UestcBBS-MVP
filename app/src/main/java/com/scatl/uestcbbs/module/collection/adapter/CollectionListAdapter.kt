package com.scatl.uestcbbs.module.collection.adapter

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.CollectionListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.module.message.MessageManager
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.widget.span.CenterImageSpan
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil

/**
 * Created by tanlei02 at 2023/5/5 11:47
 */
class CollectionListAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CollectionListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: CollectionListBean) {
        super.convert(helper, item)

        helper
            .addOnClickListener(R.id.avatar)
            .addOnClickListener(R.id.author_name)
            .addOnClickListener(R.id.latest_post_title)

        val collectionName = helper.getView<TextView>(R.id.collection_name)
        val dsp = helper.getView<TextView>(R.id.dsp)
        val tagRv = helper.getView<RecyclerView>(R.id.tag_rv)
        val avatar = helper.getView<ImageView>(R.id.avatar)
        val authorName = helper.getView<TextView>(R.id.author_name)
        val latestPostTitle = helper.getView<TextView>(R.id.latest_post_title)
        val subscribeCount = helper.getView<TextView>(R.id.subscribe_count)
        val iconNew = helper.getView<ImageView>(R.id.icon_new)

        iconNew.visibility = View.GONE
        MessageManager.INSTANCE.collectionUpdateInfo.forEach {
            if (it.cid == item.collectionId) {
                iconNew.visibility = View.VISIBLE
            }
        }

        avatar.load(item.authorAvatar)
        authorName.text = item.authorName
        subscribeCount.text = "${item.subscribeCount}订阅"

        if (item.subscribeByMe) {
            val spannableStringBuilder = SpannableStringBuilder("【订阅】" + item.collectionTitle)
            spannableStringBuilder.setSpan(ForegroundColorSpan(mContext.getColor(R.color.forum_color_1)), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            collectionName.text = spannableStringBuilder
        } else if (item.createByMe) {
            val spannableStringBuilder = SpannableStringBuilder("【创建】" + item.collectionTitle)
            spannableStringBuilder.setSpan(ForegroundColorSpan(mContext.getColor(R.color.forum_color_2)), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            collectionName.text = spannableStringBuilder
        } else {
            collectionName.text = item.collectionTitle
        }

        if (item.collectionDsp.isNullOrEmpty()) {
            dsp.visibility = View.GONE
        } else {
            dsp.visibility = View.VISIBLE
            dsp.text = item.collectionDsp
        }

        if (item.latestPostTitle.isNullOrEmpty()) {
            latestPostTitle.visibility = View.GONE
        } else {
            latestPostTitle.visibility = View.VISIBLE
            val spannableStringBuilder = SpannableStringBuilder("I" + item.latestPostTitle)
            (AppCompatResources.getDrawable(mContext, R.drawable.ic_new) as? VectorDrawable)?.let {
                it.setTint(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary))
                val radio = it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
                it.bounds = Rect(0, 0, (latestPostTitle.textSize * radio * 1.8f).toInt(), (latestPostTitle.textSize * 1.8f).toInt())
                val imageSpan = CenterImageSpan(it).apply {
                    rightPadding = ScreenUtil.dip2px(mContext, 2f)
                }
                spannableStringBuilder.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            latestPostTitle.text = spannableStringBuilder
        }

        if (item.collectionTags.isNullOrEmpty()) {
            tagRv.visibility = View.GONE
        } else {
            tagRv.apply {
                visibility = View.VISIBLE
                adapter = CollectionTagAdapter(R.layout.item_collection_tag).apply {
                    setNewData(item.collectionTags)
                }
            }
        }
    }
}