package com.scatl.uestcbbs.module.post.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.RateUserBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;

/**
 * author: sca_tl
 * date: 2021/3/31 21:13
 * description:
 */
public class P2DaShangAdapter extends BaseQuickAdapter<RateUserBean, BaseViewHolder> {
    public P2DaShangAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RateUserBean item) {
        helper.setText(R.id.item_p2_dashang_user_name, item.userName)
                .setText(R.id.item_p2_dashang_time, item.time)
                .setText(R.id.item_p2_dashang_credit, item.credit)
                .setText(R.id.item_p2_dashang_reason, item.reason)
                .addOnClickListener(R.id.item_p2_dashang_root_layout);

        ((TextView)helper.getView(R.id.item_p2_dashang_credit))
                .setTextColor(item.credit.contains("水滴") ? Color.parseColor("#108EE9") : Color.parseColor("#D3CC00"));

        GlideLoader4Common.simpleLoad(mContext, Constant.USER_AVATAR_URL + item.uid, helper.getView(R.id.item_p2_dashang_user_avatar));
    }
}
