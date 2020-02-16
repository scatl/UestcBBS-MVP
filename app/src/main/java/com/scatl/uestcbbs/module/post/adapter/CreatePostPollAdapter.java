package com.scatl.uestcbbs.module.post.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/16 15:58
 */
public class CreatePostPollAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public CreatePostPollAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.item_create_post_poll_textview, (helper.getLayoutPosition() + 1) + ". " + item);

    }
}
