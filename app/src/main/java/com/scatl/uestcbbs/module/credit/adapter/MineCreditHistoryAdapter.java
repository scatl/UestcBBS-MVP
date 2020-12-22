package com.scatl.uestcbbs.module.credit.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.MineCreditBean;

import java.util.List;

public class MineCreditHistoryAdapter extends BaseQuickAdapter<MineCreditBean.CreditHistoryBean, BaseViewHolder> {
    public MineCreditHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MineCreditBean.CreditHistoryBean item) {
        helper.setText(R.id.item_credit_history_ation, item.action)
                .setText(R.id.item_credit_history_change, item.change)
                .setText(R.id.item_credit_history_detail, item.detail)
                .setText(R.id.item_credit_history_time, item.time);

        ((TextView)helper.getView(R.id.item_credit_history_change)).setTextColor(item.increase ? Color.parseColor("#f26c4f") : Color.parseColor("#999999"));

    }
}
