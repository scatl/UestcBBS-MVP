package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.ItemAddVoteBinding
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/5/24 17:45
 */
@SuppressLint("SetTextI18n")
class AddVoteAdapter : PreloadAdapter<String, ItemAddVoteBinding>() {

    companion object {
        const val PAYLOAD_EXCHANGE = "exchange"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_ADD = "add"
    }

    override fun getViewBinding(parent: ViewGroup): ItemAddVoteBinding {
        return ItemAddVoteBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String?, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, item, payloads)
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position, item)
        } else {
            if (payloads[0] is String) {
                if (payloads[0] == PAYLOAD_EXCHANGE) {
                    setNumText(holder, item)
                } else if (payloads[0] == PAYLOAD_DELETE) {
                    setIconDelete(holder, item)
                } else if (payloads[0] == PAYLOAD_ADD) {
                    setIconDelete(holder, item)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: String?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemAddVoteBinding

        setIconDelete(holder, item)
        setNumText(holder, item)

        binding.root.background = GradientDrawable().apply {
            cornerRadius = ScreenUtil.dip2px(context, 10f).toFloat()
            color = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorSurface))
        }

        binding.num.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            color = ColorStateList.valueOf(ColorUtil.getAlphaColor(0.1f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)))
        }

        if (binding.edittext.tag is TextWatcher) {
            binding.edittext.removeTextChangedListener(binding.edittext.tag as TextWatcher)
        }
        binding.edittext.setText(item)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(e: Editable) {
                if (e.toString().isEmpty()) {
                    set(holder.layoutPosition, "")
                } else {
                    if (e.toString().length > 80) {
                        set(holder.layoutPosition, e.toString().substring(0, 80))
                    } else {
                        set(holder.layoutPosition, e.toString())
                    }
                }
            }
        }
        binding.edittext.addTextChangedListener(textWatcher)
        binding.edittext.tag = textWatcher
    }

    private fun setNumText(holder: ViewHolder, item: String?) {
        val binding = holder.binding as ItemAddVoteBinding
        binding.num.text = "${holder.adapterPosition + 1}"
    }

    private fun setIconDelete(holder: ViewHolder, item: String?) {
        val binding = holder.binding as ItemAddVoteBinding
        if (items.size > 2) {
            binding.iconDelete.visibility = View.VISIBLE
        } else {
            binding.iconDelete.visibility = View.GONE
        }
    }

}