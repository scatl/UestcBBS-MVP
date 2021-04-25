package com.scatl.uestcbbs.module.post.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
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
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:52
 */
public class PostCommentAdapter extends BaseQuickAdapter<PostDetailBean.ListBean, BaseViewHolder> {

    private int author_id;
    private int topic_id;

    public PostCommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setAuthorId (int id) {
        this.author_id = id;
    }

    public void setTopicId(int tid) {
        this.topic_id = tid;
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

        TextView floor = helper.getView(R.id.item_post_comment_floor);
        floor.setText(item.position >= 2 && item.position <= 5 ? Constant.FLOOR[item.position - 2] : mContext.getString(R.string.reply_floor, item.position));

        if (item.poststick == 1) {
            floor.setText("置顶");
            floor.setBackgroundResource(R.drawable.shape_post_detail_user_level_1);
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
            helper.getView(R.id.item_post_comment_author_level).setVisibility(View.VISIBLE);
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle);
            ((TextView) helper.getView(R.id.item_post_comment_author_level)).setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(item.userTitle)));
            helper.setText(R.id.item_post_comment_author_level, matcher.find() ? (matcher.group(2).contains("禁言") ? "禁言中" : matcher.group(2)) : item.userTitle);
        } else {
            helper.getView(R.id.item_post_comment_author_level).setVisibility(View.GONE);
//            ((TextView) helper.getView(R.id.item_post_comment_author_level)).setBackgroundTintList(ColorStateList.valueOf(ForumUtil.getLevelColor(item.userTitle)));
//            helper.setText(R.id.item_post_comment_author_level, "未知等级");
        }

        //((TextView)helper.getView(R.id.item_post_comment_author_name)).setTextColor("站长".equals(item.userTitle) ? ForumUtil.getLevelColor(item.userTitle) : mContext.getColor(R.color.colorPrimary));

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

//        TextView rewordTv = helper.getView(R.id.item_post_comment_reword_info);
//        if (item.isLoadedRewardData) {
//            rewordTv.setVisibility(View.VISIBLE);
//            rewordTv.setText(item.rewordInfo);
//        } else {
//            RetrofitCookieUtil
//                    .getInstance()
//                    .getApiService()
//                    .findPost1(topic_id, item.reply_posts_id)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            try {
//                                item.isLoadedRewardData = true;
//                                Document document = Jsoup.parse(response.body());
//                                Elements elements = document.select("div[id=post_" + item.reply_posts_id + "]").select("h3[class=psth xs1]");
//
//                                Log.e("tttttttt", elements.text()+"[["+item.reply_posts_id+"]]"+item.reply_id);
//                                if (elements.text() != null && elements.text().length() != 0) {
//                                    item.rewordInfo = elements.text();
//                                    rewordTv.setVisibility(View.VISIBLE);
//                                    rewordTv.setText(elements.text());
//                                }
//
//                            } catch (Exception e) {
//                                rewordTv.setVisibility(View.GONE);
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            rewordTv.setVisibility(View.GONE);
//                        }
//                    });
//        }


//        if (item.isLoadedDaShangData && item.isLoadedDianPingData) { //加载过打赏和点评数据了
//            //直接显示
//        } else if (!item.isLoadedDaShangData && item.isLoadedDianPingData) {  //加载过点评，没有加载过打赏
//            RetrofitCookieUtil
//                    .getInstance()
//                    .getApiService()
//                    .getAllRateUser1(item.reply_id, item.reply_posts_id)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.e("加载打赏成功1", response.code()+"");
//                            item.isLoadedDaShangData = true;
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Log.e("加载打赏失败1", t.toString()+"");
//                        }
//                    });
//        } else if (item.isLoadedDaShangData && !item.isLoadedDianPingData){  //加载过打赏，没有加载过点评
//            RetrofitCookieUtil
//                    .getInstance()
//                    .getApiService()
//                    .getCommentList1(item.reply_id, item.reply_posts_id)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.e("加载点评成功1", response.code()+"");
//                            item.isLoadedDianPingData = true;
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Log.e("加载点评失败1", t.toString()+"");
//                        }
//                    });
//
//        } else {
//            RetrofitCookieUtil
//                    .getInstance()
//                    .getApiService()
//                    .getAllRateUser1(item.reply_id, item.reply_posts_id)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.e("加载打赏成功2", response.code()+"");
//                            item.isLoadedDaShangData = true;
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Log.e("加载打赏失败2", t.toString()+"");
//                        }
//                    });
//            RetrofitCookieUtil
//                    .getInstance()
//                    .getApiService()
//                    .getCommentList1(item.reply_id, item.reply_posts_id)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.e("加载点评成功2", response.code()+"");
//                            item.isLoadedDianPingData = true;
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Log.e("加载点评失败2", t.toString()+"");
//                        }
//                    });
//        }




    }

}
