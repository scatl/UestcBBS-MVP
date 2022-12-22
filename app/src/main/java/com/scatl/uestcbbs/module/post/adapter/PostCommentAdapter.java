package com.scatl.uestcbbs.module.post.adapter;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SupportedBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ColorUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.DebugUtil;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.JsonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:52
 */
public class PostCommentAdapter extends BaseQuickAdapter<PostDetailBean.ListBean, BaseViewHolder> {

    public static class Payload {
        public static final String UPDATE_SUPPORT = "update_support";
    }

    private int author_id;
    private int topic_id;
    private List<PostDetailBean.ListBean> totalCommentData;

    public PostCommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setAuthorId (int id) {
        this.author_id = id;
    }

    public void setTopicId(int tid) {
        this.topic_id = tid;
    }

    public void setTotalCommentData(List<PostDetailBean.ListBean> totalCommentData) {
        this.totalCommentData = totalCommentData;
    }

    @Override
    protected void convertPayloads(@NonNull BaseViewHolder helper, PostDetailBean.ListBean item, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            convert(helper, item);
        } else {
            String payload = (String) payloads.get(0);

            if (Payload.UPDATE_SUPPORT.equals(payload)) {
                item.isSupported = true;
                item.supportedCount ++;
                item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(App.getContext());

                SupportedBean supportedBean = new SupportedBean();
                supportedBean.setPid(item.reply_posts_id);
                supportedBean.save();

                updateSupport(helper, item);
                updateHotImg(helper, item);
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, PostDetailBean.ListBean item) {
        helper.setText(R.id.item_post_comment_author_name, item.reply_name)
                .setText(R.id.item_post_comment_author_time, TimeUtil.formatTime(item.posts_date, R.string.post_time1, mContext))
                .addOnClickListener(R.id.item_post_comment_reply_button)
                .addOnClickListener(R.id.item_post_comment_author_avatar)
                .addOnClickListener(R.id.item_post_comment_support_button)
                .addOnClickListener(R.id.item_post_comment_more_button)
                .addOnClickListener(R.id.item_post_comment_root_rl)
                .addOnClickListener(R.id.quote_layout)
                .addOnLongClickListener(R.id.item_post_comment_root_rl);

        ImageView avatarImg = helper.getView(R.id.item_post_comment_author_avatar);
        if (item.reply_id == 0 && "匿名".equals(item.reply_name)) {
            GlideLoader4Common.simpleLoad(mContext, R.drawable.ic_anonymous, avatarImg);
        } else {
            GlideLoader4Common.simpleLoad(mContext, item.icon, avatarImg);
        }
        helper.getView(R.id.item_post_comment_author_iamauthor).setVisibility(item.reply_id == author_id && item.reply_id != 0 ? View.VISIBLE : View.GONE);

        TextView floor = helper.getView(R.id.item_post_comment_floor);
        floor.setText(item.position >= 2 && item.position <= 5 ? Constant.FLOOR[item.position - 2] : mContext.getString(R.string.reply_floor, item.position));

        if (item.poststick == 1) {
            floor.setText("置顶");
            floor.setBackgroundResource(R.drawable.shape_post_detail_user_level_1);
        } else {
            floor.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary));
            floor.setBackground(null);
        }

        TextView mobileSign = helper.getView(R.id.item_post_comment_author_mobile_sign);
        mobileSign.setText(TextUtils.isEmpty(item.mobileSign) ? "来自网页版" : item.mobileSign);

        if (item.extraPanel != null && item.extraPanel.size() > 0) {
            if ("support".equals(item.extraPanel.get(0).type)) {
                if (item.extraPanel.get(0).extParams != null) {
                    item.supportedCount = item.extraPanel.get(0).extParams.recommendAdd;
                }
            }
        }
        item.isHotComment = item.supportedCount >= SharePrefUtil.getHotCommentZanThreshold(App.getContext());
        item.isSupported = null != LitePal
                .where("pid = " + item.reply_posts_id)
                .findFirst(SupportedBean.class);

        updateSupport(helper, item);
        updateHotImg(helper, item);

        if (!TextUtils.isEmpty(item.userTitle)) {
            helper.getView(R.id.item_post_comment_author_level).setVisibility(View.VISIBLE);
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle);
            ((TextView) helper.getView(R.id.item_post_comment_author_level))
                    .setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(mContext, item.userTitle)));
            helper.setText(R.id.item_post_comment_author_level, matcher.find() ? (matcher.group(2).contains("禁言") ? "禁言中" : matcher.group(2)) : item.userTitle);
        } else {
            helper.getView(R.id.item_post_comment_author_level).setVisibility(View.GONE);
        }

        //有引用内容
        if (item.is_quote == 1) {
            PostDetailBean.ListBean data = findCommentByPid(totalCommentData, item.quote_pid);
            if (data != null) {
                helper.getView(R.id.quote_layout).setVisibility(View.VISIBLE);
                TextView quoteName = helper.getView(R.id.quote_name);
                ShapeableImageView quoteAvatar = helper.getView(R.id.quote_avatar);

                quoteName.setText(data.reply_name);
                if (data.reply_id == 0 && "匿名".equals(data.reply_name)) {
                    GlideLoader4Common.simpleLoad(mContext, R.drawable.ic_anonymous, quoteAvatar);
                } else {
                    GlideLoader4Common.simpleLoad(mContext, data.icon, quoteAvatar);
                }
                RecyclerView originRv = helper.getView(R.id.origin_comment_rv);
                PostContentAdapter postContentAdapter = new PostContentAdapter(mContext, topic_id, null);
                List<ContentViewBean> data1 = JsonUtil.modelListA2B(data.reply_content, ContentViewBean.class, data.reply_content.size());
                originRv.setAdapter(postContentAdapter);
                postContentAdapter.setData(data1);
            }
        } else {
            helper.getView(R.id.quote_layout).setVisibility(View.GONE);
        }

        RecyclerView recyclerView = helper.getView(R.id.content_rv);
        PostContentAdapter postContentAdapter = new PostContentAdapter(mContext, topic_id, null);
        List<ContentViewBean> data = JsonUtil.modelListA2B(item.reply_content, ContentViewBean.class, item.reply_content.size());
        recyclerView.setAdapter(postContentAdapter);
        postContentAdapter.setData(data);
    }

    /**
     * 更新点赞按钮
     */
    private void updateSupport(BaseViewHolder helper, PostDetailBean.ListBean item) {
        TextView support = helper.getView(R.id.item_post_comment_support_count);
        ImageView supportIcon = helper.getView(R.id.image1);
        if (item.supportedCount != 0) {
            support.setText(String.valueOf(item.supportedCount));
        } else {
            support.setText("");
            supportIcon.setImageTintList(ColorStateList.valueOf(App.getContext().getColor(R.color.image_tint)));
        }
        if (item.isSupported) {
            support.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary));
            supportIcon.setImageTintList(ColorStateList.valueOf(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary)));
        } else {
            support.setTextColor(App.getContext().getColor(R.color.image_tint));
            supportIcon.setImageTintList(ColorStateList.valueOf(App.getContext().getColor(R.color.image_tint)));
        }
    }

    /**
     * 更新热评图标
     */
    private void updateHotImg(BaseViewHolder helper, PostDetailBean.ListBean item) {
        ImageView hotImg = helper.getView(R.id.item_post_comment_hot_img);
        hotImg.setVisibility(item.isHotComment ? View.VISIBLE : View.GONE);
    }

    public PostDetailBean.ListBean findCommentByPid(List<PostDetailBean.ListBean> listBean, int pid) {
        for (int i = 0; i < listBean.size(); i ++) {
            PostDetailBean.ListBean bean = listBean.get(i);
            if (pid == bean.reply_posts_id) {
                return bean;
            }
        }
        return null;
    }

}
