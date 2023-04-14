package com.scatl.uestcbbs.module.post.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.widget.ninelayout.NineGridLayout;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/12 17:43
 */
public class HotPostAdapter extends BaseQuickAdapter<HotPostBean.ListBean, BaseViewHolder> {

    public HotPostAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addData(List<HotPostBean.ListBean> data, boolean refresh) {
        List<HotPostBean.ListBean> newList = new ArrayList<>();

        for (int i = 0; i <data.size(); i ++) {
            if (!ForumUtil.isInBlackList(data.get(i).user_id)) {
                newList.add(data.get(i));
            }
        }

        if (refresh) {
            setNewData(newList);
        } else {
            addData(newList);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, HotPostBean.ListBean item) {
        helper.setText(R.id.item_hot_post_user_name, item.user_nick_name)
                .setText(R.id.item_hot_post_board_name, item.board_name)
                .setText(R.id.item_hot_post_title, item.title)
                .setText(R.id.item_hot_post_comments_count, String.valueOf(" " + item.replies))
                .setText(R.id.item_hot_post_zan_count, String.valueOf(" " + item.recommendAdd))
                .setText(R.id.item_hot_post_view_count, String.valueOf(" " + item.hits))
                .setText(R.id.item_hot_post_content, String.valueOf(item.summary))
                .setText(R.id.item_hot_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.post_time, mContext))
                .addOnClickListener(R.id.item_hot_post_user_avatar)
                .addOnClickListener(R.id.item_hot_post_board_name);
        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_hot_post_user_avatar));

        helper.getView(R.id.item_hot_post_content).setVisibility(item.summary == null || item.summary.length() == 0 ? View.GONE : View.VISIBLE);

        NineGridLayout nineGridLayout = helper.getView(R.id.image_layout);
        if (item.imageList != null && item.imageList.size() > 0) {
            nineGridLayout.setVisibility(View.VISIBLE);
            nineGridLayout.setNineGridAdapter(new NineImageAdapter(item.imageList));
        } else {
            nineGridLayout.setVisibility(View.GONE);
        }
    }
}
