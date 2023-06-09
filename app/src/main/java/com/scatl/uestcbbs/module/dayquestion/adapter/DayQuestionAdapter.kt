package com.scatl.uestcbbs.module.dayquestion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.scatl.uestcbbs.databinding.ItemDayQuestionBinding
import com.scatl.uestcbbs.entity.DayQuestionBean
import com.scatl.uestcbbs.helper.PreloadAdapter

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: DayQuestionBean.Options?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemDayQuestionBinding

        binding.radioBtn.text = item.dsp
        binding.radioBtn.isChecked = holder.layoutPosition == checkedPosition
    }
}