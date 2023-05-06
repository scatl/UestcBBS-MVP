package com.scatl.uestcbbs.module.collection.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.Constant
import kotlin.random.Random

/**
 * Created by sca_tl at 2023/5/4 19:09
 */
class CollectionTagAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<String, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: String) {
        super.convert(helper, item)

        val text = helper.getView<TextView>(R.id.text)

        text.apply {
            this.text = item
            this.setBackgroundResource(R.drawable.shape_collection_tag)
            this.backgroundTintList = ColorStateList.valueOf(Color.parseColor(Constant.TAG_COLOR[Random.nextInt(Constant.TAG_COLOR.size)]))
        }
    }
}