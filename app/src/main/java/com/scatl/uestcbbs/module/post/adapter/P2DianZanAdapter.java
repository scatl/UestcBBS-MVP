package com.scatl.uestcbbs.module.post.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/3/31 19:56
 * description:
 */
public class P2DianZanAdapter extends BaseQuickAdapter<PostDetailBean.TopicBean.ZanListBean, BaseViewHolder> {


    public P2DianZanAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PostDetailBean.TopicBean.ZanListBean item) {
        helper.setText(R.id.item_view_voter_name, item.username)
                .addOnClickListener(R.id.item_view_voter_avatar);

        GlideLoader4Common.simpleLoad(mContext, Constant.USER_AVATAR_URL+item.recommenduid, helper.getView(R.id.item_view_voter_avatar));
    }
}
