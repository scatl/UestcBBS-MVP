package com.scatl.uestcbbs.module.board.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.TimeUtil;

public class SingleBoardAdapter extends BaseQuickAdapter<SingleBoardBean.ListBean, BaseViewHolder> {

    public SingleBoardAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleBoardBean.ListBean item) {
        helper.setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_board_name, item.board_name)
                .setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_comments_count, String.valueOf(" " + item.replies))
                .setText(R.id.item_simple_post_zan_count, String.valueOf(" " + item.recommendAdd))
                .setText(R.id.item_simple_post_content, String.valueOf(item.subject))
                .setText(R.id.item_simple_post_view_count, String.valueOf(" " + item.hits))
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar);


        helper.getView(R.id.item_simple_post_board_name).setVisibility(View.GONE);
        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);

        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_simple_post_user_avatar));
    }
}
