package com.scatl.uestcbbs.module.history.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.HistoryBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/4/7 9:14
 */
public class HistoryAdapter extends BaseItemDraggableAdapter<HistoryBean, BaseViewHolder> {

    public HistoryAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HistoryBean item) {
        helper.setText(R.id.item_history_user_name, item.user_nick_name)
                .setText(R.id.item_history_board_name, item.board_name)
                .setText(R.id.item_history_title, item.title)
                .setText(R.id.item_history_content, item.subject)
                .setText(R.id.item_history_browse_time, TimeUtil.formatTime(String.valueOf(item.browserTime), R.string.post_time1, mContext) + "浏览")
                .setText(R.id.item_history_time, TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.post_time, mContext))
                .addOnClickListener(R.id.item_history_avatar)
                .addOnClickListener(R.id.item_history_board_name);
        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_history_avatar));

    }
}
