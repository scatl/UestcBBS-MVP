package com.scatl.uestcbbs.module.user.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.uestcbbs.annotation.BlackListType;
import com.scatl.uestcbbs.util.ForumUtil;

/**
 * author: sca_tl
 * date: 2020/11/27 19:54
 * description:
 */
public class BlackListAdapter extends BaseQuickAdapter<BlackListBean, BaseViewHolder> {

    private String type;

    public BlackListAdapter(int layoutResId, @BlackListType String type) {
        super(layoutResId);
        this.type = type;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BlackListBean item) {
        helper.setText(R.id.item_black_list_name, item.userName)
                .addOnClickListener(R.id.item_black_list_delete);

        TextView textView = helper.getView(R.id.item_black_list_time);
        if (ForumUtil.getAllLocalBlackListUid().contains(item.uid)) {
            textView.setText("您已屏蔽该用户帖子");
            textView.setTextColor(mContext.getColor(R.color.colorPrimary));
        } else {
            textView.setText("您未屏蔽该用户帖子，请点击重新拉黑");
            textView.setTextColor(Color.parseColor("#cc0000"));
        }

        helper.getView(R.id.item_black_list_delete).setVisibility(type.equals(BlackListType.TYPE_LOCAL) ? View.VISIBLE : View.GONE);

        Glide.with(mContext).load(item.avatar).into((ImageView) helper.getView(R.id.item_black_list_avatar));
    }
}
