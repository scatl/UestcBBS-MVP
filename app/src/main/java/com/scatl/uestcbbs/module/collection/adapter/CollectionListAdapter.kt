package com.scatl.uestcbbs.module.collection.adapter

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemCollectionListBinding
import com.scatl.uestcbbs.entity.CollectionListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.widget.sapn.CenterImageSpan
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/5/5 11:47
 */
class CollectionListAdapter: PreloadAdapter<CollectionListBean, ItemCollectionListBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemCollectionListBinding {
        return ItemCollectionListBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: CollectionListBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemCollectionListBinding

        binding.avatar.load(item.authorAvatar)
        binding.authorName.text = item.authorName
        binding.subscribeCount.text = "${item.subscribeCount}订阅"

        if (item.subscribeByMe) {
            val spannableStringBuilder = SpannableStringBuilder("【订阅】" + item.collectionTitle)
            spannableStringBuilder.setSpan(ForegroundColorSpan(context.getColor(R.color.forum_color_1)), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

            if (item.hasUnreadPost) {
                spannableStringBuilder.append("N")
                (AppCompatResources.getDrawable(context, R.drawable.ic_new) as? VectorDrawable)?.let {
                    it.setTint(Color.RED)
                    val radio = it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
                    it.bounds = Rect(0, -2, (binding.collectionName.textSize * radio * 1.8f).toInt(), (binding.collectionName.textSize * 1.8f).toInt())
                    val imageSpan = CenterImageSpan(it).apply {
                        rightPadding = ScreenUtil.dip2px(context, 2f)
                    }
                    spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length - 1, spannableStringBuilder.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }

            binding.collectionName.text = spannableStringBuilder
        } else if (item.createByMe) {
            val spannableStringBuilder = SpannableStringBuilder("【创建】" + item.collectionTitle)
            spannableStringBuilder.setSpan(ForegroundColorSpan(context.getColor(R.color.forum_color_2)), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            binding.collectionName.text = spannableStringBuilder
        } else {
            binding.collectionName.text = item.collectionTitle
        }

        if (item.collectionDsp.isNullOrEmpty()) {
            binding.dsp.visibility = View.GONE
        } else {
            binding.dsp.visibility = View.VISIBLE
            binding.dsp.text = item.collectionDsp
        }

        if (item.latestPostTitle.isNullOrEmpty()) {
            binding.latestPostTitle.visibility = View.GONE
        } else {
            binding.latestPostTitle.visibility = View.VISIBLE
            binding.latestPostTitle.text = "最新帖子：".plus(item.latestPostTitle)
        }

        if (item.collectionTags.isNullOrEmpty()) {
            binding.tagRv.visibility = View.GONE
        } else {
            binding.tagRv.apply {
                visibility = View.VISIBLE
                adapter = CollectionTagAdapter().apply {
                    submitList(item.collectionTags)
                }
            }
        }
    }
}