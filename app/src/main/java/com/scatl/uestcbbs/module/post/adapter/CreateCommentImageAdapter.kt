package com.scatl.uestcbbs.module.post.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/20 16:12
 */
class CreateCommentImageAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<String, BaseViewHolder>(layoutResId, onPreload) {

    fun delete(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        val image = helper.getView<ImageView>(R.id.image)
        val deleteBtn = helper.getView<ImageView>(R.id.delete_btn)
        helper.addOnClickListener(R.id.delete_btn)
        image.load(item)
    }

}