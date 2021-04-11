package com.scatl.uestcbbs.module.darkroom.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.DarkRoomBean;

/**
 * author: sca_tl
 * date: 2021/4/10 12:56
 * description:
 */
public class DarkRoomAdapter extends BaseQuickAdapter<DarkRoomBean, BaseViewHolder> {
    public DarkRoomAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DarkRoomBean item) {
        helper.setText(R.id.item_dark_room_user_name, item.username)
                .setText(R.id.item_dark_room_action, item.action)
                .setText(R.id.item_dark_room_action_time, item.actionTime)
                .setText(R.id.item_dark_room_date_line, item.dateline)
                .setText(R.id.item_dark_room_reason, item.reason)
                .addOnClickListener(R.id.item_dark_room_user_name);

        TextView action = helper.getView(R.id.item_dark_room_action);
        if ("禁止发言".equals(item.action)) {
            action.setTextColor(Color.parseColor("#CCAF12"));
        } else if("禁止访问".equals(item.action)) {
            action.setTextColor(Color.parseColor("#CC4347"));
        }

    }
}
