package com.scatl.uestcbbs.module.message.adapter;

import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:55
 */
public class SystemMsgAdapter extends BaseQuickAdapter<SystemMsgBean.BodyBean.DataBean, BaseViewHolder> {
    public SystemMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.user_name, item.user_name)
                .setText(R.id.content, item.note)
                .setText(R.id.date, TimeUtil.formatTime(item.replied_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.user_icon)
                .addOnClickListener(R.id.action_btn);
        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.user_icon));

        Button button = helper.getView(R.id.action_btn);
        if (item.actions != null && item.actions.size() != 0) {
            button.setVisibility(View.VISIBLE);
            button.setText(item.actions.get(0).title);
        } else {
            button.setVisibility(View.GONE);
        }
//        if (item.has_action) {
//            helper.getView(R.id.item_system_action_btn).setVisibility(View.VISIBLE);
//            helper.setText(R.id.item_system_action_btn, item.actionBean.get(0).title);
//        } else {
//            helper.getView(R.id.item_system_action_btn).setVisibility(View.GONE);
//        }
    }
}
