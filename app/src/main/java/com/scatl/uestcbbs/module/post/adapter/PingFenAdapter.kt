package com.scatl.uestcbbs.module.post.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.RateUserBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/20 16:45
 */
class PingFenAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<RateUserBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: RateUserBean) {
        helper
            .setText(R.id.name, item.userName)
            .setText(R.id.time, item.time)
            .setText(R.id.reason, item.reason)
            .addOnClickListener(R.id.root_layout)

        val credit = helper.getView<TextView>(R.id.credit)
        val avatar = helper.getView<ImageView>(R.id.avatar)

        credit.text = item.credit
        credit.setTextColor(if (item.credit.contains("水滴"))
            Color.parseColor("#108EE9") else Color.parseColor("#D3CC00"))

        avatar.load(Constant.USER_AVATAR_URL + item.uid)
    }

}