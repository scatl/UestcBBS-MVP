package com.scatl.uestcbbs.module.user.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.BlackListBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/25 15:13
 */
class BlackListAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<BlackListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: BlackListBean) {
        super.convert(helper, item)

        val avatar = helper.getView<ImageView>(R.id.avatar)
        val name = helper.getView<TextView>(R.id.name)

        avatar.load(item.avatar)
        name.text = item.userName
    }
}