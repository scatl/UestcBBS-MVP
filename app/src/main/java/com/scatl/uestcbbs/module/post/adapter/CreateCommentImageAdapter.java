package com.scatl.uestcbbs.module.post.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/17 15:35
 */
public class CreateCommentImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public CreateCommentImageAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void delete(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.item_post_create_comment_img));
        helper.addOnClickListener(R.id.item_post_create_comment_deleta_img);
    }
}
