package com.scatl.uestcbbs.module.dayquestion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDayQuestionBinding
import com.scatl.uestcbbs.entity.DayQuestionBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder

/**
 * Created by sca_tl at 2023/6/9 16:17
 */
class DayQuestionAdapter: PreloadAdapter<DayQuestionBean.Options, ItemDayQuestionBinding>() {

    var checkedPosition = 0
        set(value) {
            field = value
            notifyItemRangeChanged(0, items.size)
        }

    override fun getViewBinding(parent: ViewGroup): ItemDayQuestionBinding {
        return ItemDayQuestionBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ItemDayQuestionBinding>, position: Int, item: DayQuestionBean.Options?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }

        holder.binding.radioBtn.text = item.dsp
        holder.binding.radioBtn.isChecked = holder.layoutPosition == checkedPosition
    }
}