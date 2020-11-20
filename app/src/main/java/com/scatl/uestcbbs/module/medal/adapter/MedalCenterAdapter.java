package com.scatl.uestcbbs.module.medal.adapter;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.MedalBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/10/7 19:06
 * description:
 */
public class MedalCenterAdapter extends BaseQuickAdapter<MedalBean.MedalCenterBean, BaseViewHolder> {
    public MedalCenterAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MedalBean.MedalCenterBean item) {
        helper.setText(R.id.item_model_center_name, item.medalName)
                .setText(R.id.item_medal_center_dsp, item.medalDsp)
                .addOnClickListener(R.id.item_model_center_get_medal_btn);

        Button button = helper.getView(R.id.item_model_center_get_medal_btn);
        if (item.buyDsp == null || item.buyDsp.length() == 0) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setText(item.buyDsp);
        }

        GlideLoader4Common.simpleLoad(mContext, item.medalIcon, helper.getView(R.id.item_model_center_icon));
    }
}
