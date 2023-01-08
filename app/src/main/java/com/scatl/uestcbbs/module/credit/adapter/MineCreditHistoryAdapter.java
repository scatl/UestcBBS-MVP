package com.scatl.uestcbbs.module.credit.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.MineCreditBean;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.CommonUtil;

import java.util.List;

public class MineCreditHistoryAdapter extends BaseQuickAdapter<MineCreditBean.CreditHistoryBean, BaseViewHolder> {

    public MineCreditHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MineCreditBean.CreditHistoryBean item) {
        helper.setText(R.id.action, "操作：" + item.action)
                .setText(R.id.change, item.change)
                .setText(R.id.detail, item.detail)
                .setText(R.id.time, "时间：" + item.time);

        View root = helper.getView(R.id.root);
        root.getBackground().setAlpha((int) (255 * 0.4));

        TextView change = helper.getView(R.id.change);
        change.setText("变更：");
        SpannableString spannableString = new SpannableString(item.change);
        spannableString.setSpan(new AbsoluteSizeSpan((int) (change.getTextSize() + 12), false), 0, item.change.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        change.append(spannableString);
        change.setTextColor(item.increase ? mContext.getResources().getColor(R.color.forum_color_1) : ColorUtil.getAttrColor(mContext, R.attr.colorOutline));
    }
}
