package com.scatl.uestcbbs.module.post.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.ViewWarningItem
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/12 13:32
 */
class ViewWarningAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<ViewWarningItem, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: ViewWarningItem) {
        val reason = helper.getView<TextView>(R.id.reason)

        helper
            .setText(R.id.name, item.name)
            .setText(R.id.date, item.time)
            .addOnClickListener(R.id.root_layout)

        if (item.reason.isNullOrEmpty()) {
            reason.visibility = View.GONE
        } else {
            reason.text = item.reason
            reason.visibility = View.VISIBLE
        }

        helper.getView<ImageView>(R.id.avatar).load(item.avatar)
    }

}