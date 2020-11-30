package com.scatl.uestcbbs.module.post.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.ImageUtil;
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


    }

}
