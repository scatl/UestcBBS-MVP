package com.scatl.uestcbbs.module.post.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:52
 */
public class PostCommentAdapter extends BaseQuickAdapter<PostDetailBean.ListBean, BaseViewHolder> {

//    private onImageClickListener onImageClickListener;
    private int author_id;

    public PostCommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setAuthorId (int id) {
        this.author_id = id;
    }

    @Override
    protected void convert(BaseViewHolder helper, PostDetailBean.ListBean item) {
        helper.setText(R.id.item_post_comment_author_name, item.reply_name)
                .setText(R.id.item_post_comment_author_time,
                        TimeUtil.formatTime(item.posts_date, R.string.post_time1, mContext))
                .setText(R.id.item_post_comment_floor, mContext.getString(R.string.reply_floor, helper.getLayoutPosition() + 1))
                .addOnClickListener(R.id.item_post_comment_reply_button)
                .addOnClickListener(R.id.item_post_comment_author_avatar)
                .addOnClickListener(R.id.item_post_comment_support_button);

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_post_comment_author_avatar));

        TextView mobileSign = helper.getView(R.id.item_post_comment_author_mobile_sign);
        mobileSign.setText(TextUtils.isEmpty(item.mobileSign) ? "来自网页版" : item.mobileSign);

        TextView support = helper.getView(R.id.item_post_comment_support_count);
        if (item.extraPanel.get(0).type.equals("support")) {
            if (item.extraPanel.get(0).extParams.recommendAdd != 0) {
                support.setText(String.valueOf(item.extraPanel.get(0).extParams.recommendAdd));
            }
        }

        helper.getView(R.id.item_post_comment_author_iamauthor).setVisibility(item.reply_id == author_id ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(item.userTitle)) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle);
            if (matcher.find()) {
                helper.setText(R.id.item_post_comment_author_level, matcher.group(2));
            } else {
                helper.setText(R.id.item_post_comment_author_level, item.userTitle);
            }
            //helper.getView(R.id.item_post_comment_author_level).setBackgroundResource(R.drawable.shape_post_detail_user_level);
        } else {
            helper.setText(R.id.item_post_comment_author_level, item.reply_name);
            //helper.getView(R.id.item_post_comment_author_level).setBackgroundResource(R.drawable.shape_post_detail_user_level);
        }

        //有引用内容
        if (item.is_quote == 1) {

            Matcher matcher = Pattern.compile("(.*?)发表于(.*?)\n(.*)").matcher(item.quote_content);
            if (matcher.find()) {
                String name = matcher.group(1).trim();
                String time = matcher.group(2).trim();
                String content = matcher.group(3);

                String time__ = TimeUtil.formatTime(
                        String.valueOf(TimeUtil.getMilliSecond(time, "yyyy-MM-dd HH:mm")),
                        R.string.post_time1,
                        mContext);

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
//        ((ContentView)helper.getView(R.id.item_post_comment_content)).setOnImageClickListener((view, urls, selected) -> onImageClickListener.onImgClick(view, urls, selected));

    }

//    public interface onImageClickListener {
//        void onImgClick(View view, List<String> urls, int selected);
//    }
//
//    public void setOnImgClickListener(onImageClickListener onImageClickListener){
//        this.onImageClickListener = onImageClickListener;
//    }
}
