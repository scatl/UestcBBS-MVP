package com.scatl.uestcbbs.module.dayquestion.adapter;

import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.DayQuestionBean;

/**
 * author: sca_tl
 * date: 2020/5/21 15:37
 * description:
 */
public class DayQuestionAdapter extends BaseQuickAdapter<DayQuestionBean.Options, BaseViewHolder> {

    private int checkedPosition;

    public DayQuestionAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setCheckedPosition(int position) {
        this.checkedPosition = position;
        notifyItemRangeChanged(0, getData().size());
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DayQuestionBean.Options item) {
        helper.addOnClickListener(R.id.item_day_question_radio_btn);
        RadioButton radioButton = helper.getView(R.id.item_day_question_radio_btn);
        radioButton.setText(item.dsp);
        radioButton.setChecked(helper.getLayoutPosition() == checkedPosition);

    }
}
