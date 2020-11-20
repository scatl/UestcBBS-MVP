package com.scatl.uestcbbs.module.magic.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

public class MagicShopAdapter extends BaseQuickAdapter<MagicShopBean.ItemList, BaseViewHolder> {
    public MagicShopAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MagicShopBean.ItemList item) {
        helper.setText(R.id.item_magic_shop_dsp, item.dsp)
                .setText(R.id.item_magic_shop_name, item.name)
                .setText(R.id.item_magic_shop_price, item.price)
                .addOnClickListener(R.id.item_magic_shop_buy_btn);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_magic_shop_icon));
    }
}
