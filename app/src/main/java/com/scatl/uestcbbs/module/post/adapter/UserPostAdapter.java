package com.scatl.uestcbbs.module.post.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 12:37
 */
public class UserPostAdapter extends BaseQuickAdapter<UserPostBean.ListBean, BaseViewHolder> {

    public UserPostAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserPostBean.ListBean item) {
        helper.setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_board_name, item.board_name)
                .setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_comments_count, String.valueOf(" " + item.replies))
                .setText(R.id.item_simple_post_zan_count, String.valueOf(" 0"))
                .setText(R.id.item_simple_post_content, String.valueOf(item.subject))
                .setText(R.id.item_simple_post_view_count, String.valueOf(" " + item.hits))
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar)
                .addOnClickListener(R.id.item_simple_post_board_name);

        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(View.GONE);

        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_simple_post_user_avatar));
    }
}
