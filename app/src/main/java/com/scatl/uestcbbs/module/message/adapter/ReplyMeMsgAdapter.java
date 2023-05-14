package com.scatl.uestcbbs.module.message.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.manager.MessageManager;
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
        helper.setText(R.id.user_name, item.user_name)
                .setText(R.id.board_name, "来自板块:" + item.board_name)
                .setText(R.id.reply_content, (item.reply_content == null ? "" : item.reply_content)
                        .replace("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n", "\n")
                        .replace("\r\n", "")
                )
                .setText(R.id.subject_title, item.topic_subject)
                .setText(R.id.subject_content,  (item.topic_content == null ? "" : item.topic_content)
                        .replace("\r\n", "")
                )
                .setText(R.id.reply_date, TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon)
                .addOnClickListener(R.id.subject_detail)
                .addOnClickListener(R.id.reply_btn)
                .addOnClickListener(R.id.board_name);
        Glide.with(mContext).load(item.icon).into((ImageView) helper.getView(R.id.user_icon));

        //显示未读标志
        helper.getView(R.id.new_msg_img).setVisibility(
                helper.getLayoutPosition() < MessageManager.Companion.getINSTANCE().getReplyUnreadCount() ?
                        View.VISIBLE : View.GONE);
    }
}
