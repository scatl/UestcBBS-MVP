package com.scatl.uestcbbs.module.post.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

public class PostDianPingAdapter extends BaseQuickAdapter<PostDianPingBean, BaseViewHolder> {

    public PostDianPingAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PostDianPingBean item) {
        helper.setText(R.id.item_post_detail_dianping_name, item.userName)
                .setText(R.id.item_post_detail_dianping_comment, item.comment)
                .setText(R.id.item_post_detail_dianping_date, item.date);
        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_post_detail_dianping_avatar));
    }
}
