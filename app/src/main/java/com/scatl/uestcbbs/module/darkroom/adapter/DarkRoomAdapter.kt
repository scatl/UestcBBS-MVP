package com.scatl.uestcbbs.module.darkroom.adapter

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.DarkRoomBean
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * Created by sca_tl at 2023/6/6 16:06
 */
class DarkRoomAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<DarkRoomBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: DarkRoomBean) {
        super.convert(helper, item)
        helper
            .setText(R.id.user_name, item.username)
            .setText(R.id.action_time, item.actionTime)
            .setText(R.id.date_line, item.dateline)
            .setText(R.id.reason, item.reason)
            .addOnClickListener(R.id.user_name)

        val action = helper.getView<TextView>(R.id.action)
        action.apply {
            text = item.action
            setTextColor(
                when(text) {
                    "禁止发言" -> { Color.parseColor("#CCAF12") }
                    "禁止访问" -> { Color.parseColor("#CC4347") }
                    else -> { Color.parseColor("#DDDDDD") }
                }
            )
        }

    }
}