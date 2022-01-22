package com.scatl.uestcbbs.module.user.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:52
 */
public class UserFriendAdapter extends BaseQuickAdapter<UserFriendBean.ListBean, BaseViewHolder> {

    public UserFriendAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserFriendBean.ListBean item) {
        helper.setText(R.id.item_user_friend_name, item.name)
                .setText(R.id.item_user_friend_last_login,
                        TimeUtil.formatTime(item.lastLogin, R.string.last_login_time, mContext));
        Glide.with(mContext).load(item.icon).into((ShapeableImageView) helper.getView(R.id.item_user_friend_icon));
    }
}
