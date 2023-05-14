package com.scatl.uestcbbs.module.houqin.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.HouQinReportListBean;

/**
 * author: sca_tl
 * date: 2020/10/24 11:16
 * description:
 */
public class HouQinReportListAdapter extends BaseQuickAdapter<HouQinReportListBean.TopicBean, BaseViewHolder> {
    public HouQinReportListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HouQinReportListBean.TopicBean item) {
        helper.setText(R.id.item_houqin_report_list_username, "发帖人：" + item.account)
                .setText(R.id.item_houqin_report_list_title, item.title)
                .setText(R.id.item_houqin_report_list_time, "受理时间：" + item.topDate)
                .setText(R.id.item_houqin_report_list_updatetime, "更新时间：" + item.replyDate)
                .setText(R.id.item_houqin_report_list_view_count, "浏览/回复：" + item.readOrReply);

        helper.getView(R.id.item_houqin_report_list_replied_pic).setVisibility("已回复".equals(item.state) ? View.VISIBLE : View.GONE);
    }
}
