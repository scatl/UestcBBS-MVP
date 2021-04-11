package com.scatl.uestcbbs.module.post.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PostWebBean;

public class PostCollectionAdapter extends BaseQuickAdapter<PostWebBean.Collection, BaseViewHolder> {
    public PostCollectionAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PostWebBean.Collection item) {
        helper.setText(R.id.item_post_detail_collection_name, item.name)
                .setText(R.id.item_post_detail_collection_subscribe_count, item.subscribeCount);
    }
}
