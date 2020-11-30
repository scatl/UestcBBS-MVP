package com.scatl.uestcbbs.module.post.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:52
 */
public class PostCommentAdapter extends BaseQuickAdapter<PostDetailBean.ListBean, BaseViewHolder> {

    private int author_id;

    public PostCommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setAuthorId (int id) {
        this.author_id = id;
    }

    public void addData(List<PostDetailBean.ListBean> data, boolean refresh) {
        List<PostDetailBean.ListBean> newList = new ArrayList<>();

        for (int i = 0; i <data.size(); i ++) {
            if (!ForumUtil.isInBlackList(data.get(i).reply_id)) {
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
    protected void convert(BaseViewHolder helper, PostDetailBean.ListBean item) {
        helper.setText(R.id.item_post_comment_author_name, item.reply_name)
                .setText(R.id.item_post_comment_author_time, TimeUtil.formatTime(item.posts_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.item_post_comment_reply_button)
                .addOnClickListener(R.id.item_post_comment_author_avatar)
                .addOnClickListener(R.id.item_post_comment_buchong_button)
                .addOnClickListener(R.id.item_post_comment_support_button)
                .addOnClickListener(R.id.item_post_comment_more_button)
                .addOnClickListener(R.id.item_post_comment_root_rl)
                .addOnLongClickListener(R.id.item_post_comment_root_rl);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_post_comment_author_avatar));
        helper.getView(R.id.item_post_comment_author_iamauthor).setVisibility(item.reply_id == author_id && item.reply_id != 0 ? View.VISIBLE : View.GONE);
        //helper.getView(R.id.item_post_comment_buchong_button).setVisibility(item.reply_id == SharePrefUtil.getUid(mContext) ? View.VISIBLE : View.GONE);
        //helper.getView(R.id.item_post_comment_support_button).setVisibility(item.reply_id == SharePrefUtil.getUid(mContext) ? View.GONE : View.VISIBLE);

        TextView floor = helper.getView(R.id.item_post_comment_floor);
        floor.setText(item.position >= 2 && item.position <= 5 ? Constant.FLOOR[item.position - 2] : mContext.getString(R.string.reply_floor, item.position));

        if (item.poststick == 1) {
            floor.setTextColor(Color.WHITE);
            floor.setText("置顶");
            floor.setBackgroundResource(R.drawable.shape_common_textview_background_not_clickable);
        } else {
            floor.setTextColor(mContext.getColor(R.color.colorPrimary));
            floor.setBackground(null);
        }

        TextView mobileSign = helper.getView(R.id.item_post_comment_author_mobile_sign);
        mobileSign.setText(TextUtils.isEmpty(item.mobileSign) ? "来自网页版" : item.mobileSign);

        TextView support = helper.getView(R.id.item_post_comment_support_count);
        if ("support".equals(item.extraPanel.get(0).type) && item.extraPanel.get(0).extParams.recommendAdd != 0) {
            support.setText(String.valueOf(item.extraPanel.get(0).extParams.recommendAdd));
        } else {
            support.setText("");
        }

        if (!TextUtils.isEmpty(item.userTitle)) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle);
            helper.setText(R.id.item_post_comment_author_level, matcher.find() ? matcher.group(2) : item.userTitle);
        } else {
            helper.setText(R.id.item_post_comment_author_level, "未知等级");
        }

        //有引用内容
        if (item.is_quote == 1) {

            Matcher matcher = Pattern.compile("(.*?)发表于(.*?)\n(.*)").matcher(item.quote_content);
            if (matcher.find()) {
                String name = matcher.group(1).trim();
                String time = matcher.group(2).trim();
                String content = matcher.group(3);

                String time__ = TimeUtil.formatTime(String.valueOf(TimeUtil.getMilliSecond(time, "yyyy-MM-dd HH:mm")), R.string.post_time1, mContext);

                helper.getView(R.id.item_post_comment_reply_to_rl).setVisibility(View.VISIBLE);
                helper.setText(R.id.item_post_comment_reply_to_rl_text, mContext.getString(R.string.quote_content, name, time__, content));
            } else {
                helper.getView(R.id.item_post_comment_reply_to_rl).setVisibility(View.VISIBLE);
                helper.setText(R.id.item_post_comment_reply_to_rl_text, item.quote_content);
            }

        } else {
            helper.getView(R.id.item_post_comment_reply_to_rl).setVisibility(View.GONE);
        }

        ((ContentView)helper.getView(R.id.item_post_comment_content)).setContentData(JsonUtil.modelListA2B(item.reply_content, ContentViewBean.class, item.reply_content.size()));

    }

}
