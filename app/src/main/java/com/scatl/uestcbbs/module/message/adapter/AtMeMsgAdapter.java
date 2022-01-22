package com.scatl.uestcbbs.module.message.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 13:08
 */
public class AtMeMsgAdapter extends BaseQuickAdapter<AtMsgBean.BodyBean.DataBean, BaseViewHolder> {

    public AtMeMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AtMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.item_at_me_name, item.user_name)
                .setText(R.id.item_at_me_reply_content, item.reply_content.replaceAll("\r\n", ""))
                .setText(R.id.item_at_me_board_name, item.board_name)
                .setText(R.id.item_at_me_subject, "主题：" + item.topic_subject)
                .setText(R.id.item_at_me_content, "主题内容：" + item.topic_content.trim())
                .setText(R.id.item_at_me_time,
                        TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.item_at_me_icon)
                .addOnClickListener(R.id.item_at_me_board_name);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_at_me_icon));
    }
}
