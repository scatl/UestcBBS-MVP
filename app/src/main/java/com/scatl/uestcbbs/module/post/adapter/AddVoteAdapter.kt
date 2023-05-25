package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/5/24 17:45
 */
@SuppressLint("SetTextI18n")
class AddVoteAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) : PreloadAdapter<String, BaseViewHolder>(layoutResId, onPreload) {

    companion object {
        const val PAYLOAD_EXCHANGE = "exchange"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_ADD = "add"
    }
    
    override fun convertPayloads(helper: BaseViewHolder, item: String, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            convert(helper, item)
        } else {
            val payload = payloads[0] as String
            if (payload == PAYLOAD_EXCHANGE) {
                setNumText(helper, item)
            } else if (payload == PAYLOAD_DELETE) {
                setIconDelete(helper, item)
            } else if (payload == PAYLOAD_ADD) {
                setIconDelete(helper, item)
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        super.convert(helper, item)
        helper.addOnClickListener(R.id.icon_delete)
        helper.addOnClickListener(R.id.icon_drag)
        helper.addOnLongClickListener(R.id.icon_drag)

        val editText = helper.getView<EditText>(R.id.edittext)
        val num = helper.getView<TextView>(R.id.num)

        setIconDelete(helper, item)

        helper.itemView.background = GradientDrawable().apply {
            cornerRadius = ScreenUtil.dip2px(mContext, 10f).toFloat()
            color = ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorSurface))
        }

        num.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            color = ColorStateList.valueOf(ColorUtil.getAlphaColor(0.1f, ColorUtil.getAttrColor(mContext, R.attr.colorPrimary)))
        }
        setNumText(helper, item)

        if (editText.tag is TextWatcher) {
            editText.removeTextChangedListener(editText.tag as TextWatcher)
        }
        editText.setText(item)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(e: Editable) {
                if (e.toString().isEmpty()) {
                    data[helper.layoutPosition] = ""
                } else {
                    if (e.toString().length > 80) {
                        data[helper.layoutPosition] = e.toString().substring(0, 80)
                    } else {
                        data[helper.layoutPosition] = e.toString()
                    }
                }
            }
        }
        editText.addTextChangedListener(textWatcher)
        editText.tag = textWatcher
    }

    private fun setNumText(helper: BaseViewHolder, item: String) {
        val num = helper.getView<TextView>(R.id.num)
        num.text = "${helper.adapterPosition + 1}"
    }

    private fun setIconDelete(helper: BaseViewHolder, item: String) {
        val iconDelete = helper.getView<ImageView>(R.id.icon_delete)
        if (data.size > 2) {
            iconDelete.visibility = View.VISIBLE
        } else {
            iconDelete.visibility = View.GONE
        }
    }

}