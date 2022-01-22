package com.scatl.uestcbbs.module.user.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.VisitorsBean;

public class UserVisitorAdapter extends BaseQuickAdapter<VisitorsBean, BaseViewHolder> {

    int mineId;

    public UserVisitorAdapter(int layoutResId, int mineId) {
        super(layoutResId);
        this.mineId = mineId;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VisitorsBean item) {
        helper.setText(R.id.item_user_visitor_name, item.visitorName)
                .setText(R.id.item_user_visitor_time, item.visitedTime)
                .addOnClickListener(R.id.item_user_visitor_delete);
        Glide.with(mContext).load(item.visitorAvatar).into((ShapeableImageView) helper.getView(R.id.item_user_visitor_icon));
        helper.getView(R.id.item_user_visitor_delete).setVisibility(mineId == item.visitorUid ? View.VISIBLE : View.GONE);
    }
}
