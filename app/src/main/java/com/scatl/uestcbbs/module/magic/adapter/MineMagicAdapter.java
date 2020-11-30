package com.scatl.uestcbbs.module.magic.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.MineMagicBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

public class MineMagicAdapter extends BaseQuickAdapter<MineMagicBean.ItemList, BaseViewHolder> {
    public MineMagicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MineMagicBean.ItemList item) {
        helper.setText(R.id.item_mine_magic_dsp, item.dsp)
                .setText(R.id.item_mine_magic_name, item.name)
                .setText(R.id.item_mine_magic_weight, "数量：" + item.totalCount + "  重量：" + item.totalWeight)
                .addOnClickListener(R.id.item_mine_magic_use_btn);

        helper.getView(R.id.item_mine_magic_use_btn).setVisibility(item.showUseBtn ? View.VISIBLE : View.GONE);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_mine_magic_icon));
    }
}
