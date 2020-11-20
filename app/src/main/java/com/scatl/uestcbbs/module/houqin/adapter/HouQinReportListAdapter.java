package com.scatl.uestcbbs.module.houqin.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.HouQinReportListBean;
import com.scatl.uestcbbs.module.houqin.model.HouQinModel;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

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

        TagFlowLayout tagFlowLayout = helper.getView(R.id.item_houqin_report_list_dep_and_campus);
        List<String> list = new ArrayList<String>(){{add(item.categName);add(item.replyDept);}};
        tagFlowLayout.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView textView = new TextView(mContext);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(12);
                textView.setText(o);
                textView.setTextColor(mContext.getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(mContext.getColor(R.color.colorPrimary));
            }
        });

    }
}
