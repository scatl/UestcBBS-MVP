package com.scatl.uestcbbs.module.user.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AlbumListBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 13:53
 */
public class AlbumListAdapter extends BaseQuickAdapter<AlbumListBean.ListBean, BaseViewHolder> {
    public AlbumListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AlbumListBean.ListBean item) {
        Glide.with(mContext).load(item.thumb_pic).into((ImageView) helper.getView(R.id.item_album_list_image));
        helper.setText(R.id.item_album_list_title, item.title);
    }
}
