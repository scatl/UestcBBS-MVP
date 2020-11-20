package com.scatl.uestcbbs.module.houqin.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/10/25 17:58
 * description:
 */
public class HouQinReportTopicImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public HouQinReportTopicImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.item_post_create_comment_img);
        GlideLoader4Common.simpleLoad(mContext, item+"", helper.getView(R.id.item_post_create_comment_img));
        helper.getView(R.id.item_post_create_comment_deleta_img).setVisibility(View.GONE);
    }
}
