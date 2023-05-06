package com.scatl.uestcbbs.module.collection.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.Constant
import com.scatl.util.ColorUtil
import kotlin.random.Random

/**
 * Created by sca_tl at 2023/5/4 20:17
 */
class CollectionSameOwnerAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<CollectionDetailBean.SameOwnerCollection, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: CollectionDetailBean.SameOwnerCollection) {
        super.convert(helper, item)
        val text = helper.getView<TextView>(R.id.text)

        text.apply {
            this.text = item.name
            this.setBackgroundResource(R.drawable.shape_collection_tag)
            this.backgroundTintList = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorSurfaceVariant))
        }
    }
}