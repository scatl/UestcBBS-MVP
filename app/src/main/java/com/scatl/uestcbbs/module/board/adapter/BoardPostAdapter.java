package com.scatl.uestcbbs.module.board.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.widget.ninelayout.NineGridLayout;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.post.adapter.NineImageAdapter;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class BoardPostAdapter extends BaseQuickAdapter<SingleBoardBean.ListBean, BaseViewHolder> {

    public BoardPostAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addData(List<SingleBoardBean.ListBean> data, boolean refresh) {
        List<SingleBoardBean.ListBean> newList = new ArrayList<>();

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
    protected void convert(BaseViewHolder helper, SingleBoardBean.ListBean item) {
        helper.setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_board_name, item.board_name)
                .setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_comments_count, String.valueOf(" " + item.replies))
                .setText(R.id.item_simple_post_zan_count, String.valueOf(" " + item.recommendAdd))
                .setText(R.id.item_simple_post_content, String.valueOf(item.subject))
                .setText(R.id.item_simple_post_view_count, String.valueOf(" " + item.hits))
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar);

        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.item_simple_post_content).setVisibility(item.subject == null || item.subject.length() == 0 ? View.GONE : View.VISIBLE);

        ImageView avatarImg = helper.getView(R.id.item_simple_post_user_avatar);
        if (item.user_id == 0 && "匿名".equals(item.user_nick_name)) {
            GlideLoader4Common.simpleLoad(mContext, R.drawable.ic_anonymous, avatarImg);
        } else {
            GlideLoader4Common.simpleLoad(mContext, item.userAvatar, avatarImg);
        }

        NineGridLayout nineGridLayout = helper.getView(R.id.image_layout);
        if (item.imageList != null && item.imageList.size() > 0) {
            nineGridLayout.setVisibility(View.VISIBLE);
            nineGridLayout.setNineGridAdapter(new NineImageAdapter(item.imageList));
        } else {
            nineGridLayout.setVisibility(View.GONE);
        }

    }
}


