package com.scatl.uestcbbs.module.home.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.OnLineUserBean;

/**
 * author: sca_tl
 * date: 2020/10/7 11:32
 * description:
 */
public class OnLineUserAdapter extends BaseQuickAdapter<OnLineUserBean.UserBean, BaseViewHolder> {
    public OnLineUserAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, OnLineUserBean.UserBean item) {
        helper.setText(R.id.item_online_user_name, item.userName)
                .setText(R.id.item_online_user_time, item.time);
        Glide.with(mContext).load(item.userAvatar).into((ImageView) helper.getView(R.id.item_online_user_avatar));
    }
}
