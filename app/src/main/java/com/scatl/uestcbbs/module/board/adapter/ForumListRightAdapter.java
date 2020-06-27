package com.scatl.uestcbbs.module.board.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.GridView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:31
 */
public class ForumListRightAdapter extends BaseQuickAdapter<ForumListBean.ListBean, BaseViewHolder> {

    public ForumListRightAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ForumListBean.ListBean item) {
        helper.setText(R.id.forum_list_right_title, item.board_category_name);
        ForumListGridViewAdapter forumListGridViewAdapter = new ForumListGridViewAdapter(mContext, getData().get(helper.getLayoutPosition()).board_list);
        GridView gridView = helper.getView(R.id.forum_list_right_gridview);
        gridView.setNumColumns(SharePrefUtil.getBoardListColumns(mContext));
        gridView.setAdapter(forumListGridViewAdapter);
        gridView.requestFocus();
    }
}
