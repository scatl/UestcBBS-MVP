package com.scatl.uestcbbs.module.search.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.card.MaterialCardView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.header.material.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 18:32
 */
public class SearchPostAdapter extends BaseQuickAdapter<SearchPostBean.ListBean, BaseViewHolder> {

    private boolean hideAnonymousPost;

    public SearchPostAdapter(int layoutResId, boolean hideAnonymousPost) {
        super(layoutResId);
        this.hideAnonymousPost = hideAnonymousPost;
    }

    public void addSearchPostData(List<SearchPostBean.ListBean> data, boolean refresh) {
        List<SearchPostBean.ListBean> newList = new ArrayList<>();
        for (int i = 0; i < data.size(); i ++) {
            if (data.get(i).user_nick_name == null ||
                    data.get(i).user_nick_name.length() == 0 && hideAnonymousPost) {
                data.get(i).user_nick_name = Constant.ANONYMOUS_NAME;
                data.get(i).avatar = Constant.DEFAULT_AVATAR;
                data.get(i).user_id = 0;
            } else {
                data.get(i).avatar = Constant.USER_AVATAR_URL + data.get(i).user_id;
            }
            newList.add(data.get(i));
        }

        if (refresh) {
            setNewData(newList);
        } else {
            addData(newList);
        }
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

        ((MaterialCardView)helper.getView(R.id.item_simple_post_card_view))
                .setCardBackgroundColor(ColorUtil.getAttrColor(mContext, R.attr.colorOnSurfaceInverse));
        helper.getView(R.id.item_simple_post_board_name).setVisibility(View.GONE);
        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);

        ImageView avatarImg = helper.getView(R.id.item_simple_post_user_avatar);
        if (item.user_id == 0 && "匿名".equals(item.user_nick_name)) {
            Glide.with(mContext).load(R.drawable.ic_anonymous).into(avatarImg);
        } else {
            Glide.with(mContext).load(item.avatar).into(avatarImg);
        }
    }
}
