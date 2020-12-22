package com.scatl.uestcbbs.module.update.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/12/19 12:18
 * description:
 */
public class UpdateImgAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public UpdateImgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        GlideLoader4Common.loadIntoTarget(mContext, item, helper.getView(R.id.item_update_img_image));
    }
}
