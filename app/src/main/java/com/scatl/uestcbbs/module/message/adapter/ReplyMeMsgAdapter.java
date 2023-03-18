package com.scatl.uestcbbs.module.message.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.module.message.MessageManager;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 15:27
 */
public class ReplyMeMsgAdapter extends BaseQuickAdapter<ReplyMeMsgBean.BodyBean.DataBean, BaseViewHolder> {
    public ReplyMeMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReplyMeMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.item_reply_me_user_name, item.user_name)
                .setText(R.id.item_reply_me_board_name, item.board_name)
                .setText(R.id.item_reply_me_reply_content, (item.reply_content == null ? "" : item.reply_content)
                        .replace("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n", "\n")
                        .replace("\r\n", ""))
                .setText(R.id.item_reply_me_quote_title, item.topic_subject)
                .setText(R.id.item_reply_me_quote_content, item.topic_content)
                .setText(R.id.item_reply_me_reply_date,
                        TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.item_reply_me_user_icon)
                .addOnClickListener(R.id.item_reply_me_quote_rl)
                .addOnClickListener(R.id.item_reply_me_reply_btn)
                .addOnClickListener(R.id.item_reply_me_board_name);
        Glide.with(mContext).load(item.icon).into((ImageView) helper.getView(R.id.item_reply_me_user_icon));

        //显示未读标志
        helper.getView(R.id.item_reply_me_new_msg_img).setVisibility(
                helper.getLayoutPosition() < MessageManager.Companion.getINSTANCE().getReplyUnreadCount() ?
                        View.VISIBLE : View.GONE);
    }
}
