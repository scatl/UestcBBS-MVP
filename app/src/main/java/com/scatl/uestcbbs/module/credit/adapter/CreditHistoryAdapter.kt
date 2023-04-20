package com.scatl.uestcbbs.module.credit.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.MineCreditBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.util.ColorUtil

/**
 * Created by sca_tl at 2023/4/12 14:44
 */
class CreditHistoryAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<MineCreditBean.CreditHistoryBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: MineCreditBean.CreditHistoryBean) {
        val root = helper.getView<View>(R.id.root)
        val change = helper.getView<TextView>(R.id.change)
        val action = helper.getView<TextView>(R.id.action)
        val detail = helper.getView<TextView>(R.id.detail)
        val time = helper.getView<TextView>(R.id.time)

        root.background.alpha = (255 * 0.4).toInt()
        action.text = "操作：".plus(item.action)
        detail.text = item.detail
        time.text = "时间：".plus(item.time)

        change.text = "变更："
        val spannableString = SpannableString(item.change).apply {
            setSpan(
                AbsoluteSizeSpan((change.textSize + 12).toInt(), false), 0, item.change.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        change.append(spannableString)
        change.setTextColor(
            if (item.increase) ContextCompat.getColor(mContext, R.color.forum_color_1)
            else ColorUtil.getAttrColor(mContext, R.attr.colorOutline)
        )
    }
}