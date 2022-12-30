package com.scatl.uestcbbs.module.message.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.widget.span.MyClickableSpan;
import com.scatl.uestcbbs.entity.DianPingMessageBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;

/**
 * author: sca_tl
 * date: 2021/4/18 18:17
 * description:
 */
public class DianPingMsgAdapter extends BaseQuickAdapter<DianPingMessageBean, BaseViewHolder> {
    public DianPingMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DianPingMessageBean item) {

        helper.setText(R.id.item_dianping_msg_name, item.userName)
                .setText(R.id.item_dianping_msg_time, item.time)
                .addOnClickListener(R.id.item_dianping_msg_view_dianping_btn);
        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_dianping_msg_avatar));

        SpannableString spannableString = new SpannableString(item.topicTitle);
        MyClickableSpan clickableSpan = new MyClickableSpan(mContext, Constant.TOPIC_URL + item.tid, false);
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        TextView content = helper.getView(R.id.item_dianping_msg_content);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setText("点评了您在主题《");
        content.append(spannableString);
        content.append("》发表的帖子");

    }
}
