package com.scatl.uestcbbs.module.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 18:32
 */
public class SearchPostAdapter extends BaseQuickAdapter<SearchPostBean.ListBean, BaseViewHolder> {

    public SearchPostAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchPostBean.ListBean item) {
        helper.setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_content, item.subject)
                .setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_comments_count, String.valueOf("  " + item.replies))
                .setText(R.id.item_simple_post_view_count, String.valueOf("  "  + item.hits))
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(item.last_reply_date, R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar);

        helper.getView(R.id.item_simple_post_board_name).setVisibility(View.GONE);
        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);
        
		String icon = mContext.getString(R.string.icon_url, item.user_id);
        Glide.with(mContext).load(icon).into((CircleImageView)helper.getView(R.id.item_simple_post_user_avatar));

    }
}
