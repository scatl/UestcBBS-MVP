package com.scatl.uestcbbs.module.message.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.entity.DianPingMsgBean;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scatl.uestcbbs.widget.span.CustomClickableSpan;
import com.scatl.uestcbbs.entity.DianPingMessageBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/4/18 18:17
 * description:
 */
public class DianPingMsgAdapter extends BaseQuickAdapter<DianPingMsgBean.BodyBean.DataBean, BaseViewHolder> {

    public DianPingMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DianPingMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.user_name, item.comment_user_name)
                .setText(R.id.reply_content, "点评了你的帖子，点击查看")
                .setText(R.id.board_name, "来自板块:" + item.board_name)
                .setText(R.id.subject_title, item.topic_subject)
                .setText(R.id.subject_content, item.reply_content.trim())
                .setText(R.id.reply_date,
                        TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon)
                .addOnClickListener(R.id.board_name);

        GlideLoader4Common.simpleLoad(mContext, Constant.USER_AVATAR_URL + item.comment_user_id, helper.getView(R.id.user_icon));
    }
}
