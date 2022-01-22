package com.scatl.uestcbbs.module.post.adapter;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.CommonUtil;

/**
 * author: sca_tl
 * date: 2021/4/4 16:40
 * description:
 */
public class PostRateAdapter extends BaseQuickAdapter<PostDetailBean.TopicBean.RewardBean.UserListBean, BaseViewHolder> {
    public PostRateAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PostDetailBean.TopicBean.RewardBean.UserListBean item) {
        GlideLoader4Common.simpleLoad(mContext, item.userIcon, helper.getView(R.id.item_post_rate_user_avatar));

        ImageView imageView = helper.getView(R.id.item_post_rate_user_avatar);
        RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(CommonUtil.dip2px(mContext, 35), CommonUtil.dip2px(mContext, 35));
        l.leftMargin = helper.getLayoutPosition() != 0 ? CommonUtil.px2dip(MyApplication.getContext(), -40) : 0;
        imageView.setLayoutParams(l);

    }

}
