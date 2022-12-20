package com.scatl.uestcbbs.module.home.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.image.ninelayout.NineGridLayout;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ContentDataType;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.post.adapter.NineImageAdapter;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAdapter extends BaseQuickAdapter<SimplePostListBean.ListBean, BaseViewHolder> {

    private OnImgClickListener onImgClickListener;

    public HomeAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addPostData(List<SimplePostListBean.ListBean> data, boolean refresh) {
        if (refresh) {
            setNewData(data);
        } else {//滤除相同的帖子
            List<SimplePostListBean.ListBean> filter_list = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                int top_id = data.get(i).topic_id;

                for (int j = 0; j < getData().size(); j ++) {
                    int id = getData().get(j).topic_id;
                    ids.add(id);
                }

                if (!ids.contains(top_id)) { filter_list.add(data.get(i)); }
            }
            addData(filter_list);
        }
        notifyDataSetChanged();
    }

    public void addData(List<SimplePostListBean.ListBean> data, boolean refresh) {
        List<SimplePostListBean.ListBean> newList = new ArrayList<>();

        //滤除黑名单用户
        for (int i = 0; i < data.size(); i ++) {
            if (!ForumUtil.isInBlackList(data.get(i).user_id)) {
                newList.add(data.get(i));
            }
        }

        if (refresh) {
            setNewData(newList);
        } else {
            //滤除相同的帖子
            List<SimplePostListBean.ListBean> filter_list = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < newList.size(); i ++) {
                int top_id = newList.get(i).topic_id;

                for (int j = 0; j < getData().size(); j ++) {
                    int id = getData().get(j).topic_id;
                    ids.add(id);
                }

                if (!ids.contains(top_id)) { filter_list.add(newList.get(i)); }
            }

            addData(newList);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, SimplePostListBean.ListBean item) {

        helper.setText(R.id.item_simple_post_user_name, item.user_nick_name)
                .setText(R.id.item_simple_post_board_name, item.board_name)
                .setText(R.id.item_simple_post_title, item.title)
                .setText(R.id.item_simple_post_comments_count, String.valueOf(" " + item.replies))
                .setText(R.id.item_simple_post_zan_count, String.valueOf(" " + item.recommendAdd))
                .setText(R.id.item_simple_post_content, String.valueOf(item.subject))
                .setText(R.id.item_simple_post_view_count, String.valueOf(" " + item.hits))
                .setText(R.id.item_simple_post_time,
                        TimeUtil.formatTime(String.valueOf(item.last_reply_date), R.string.reply_time, mContext))
                .addOnClickListener(R.id.item_simple_post_user_avatar)
                .addOnClickListener(R.id.item_simple_post_board_name);

        helper.getView(R.id.item_simple_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.item_simple_post_content).setVisibility(item.subject == null || item.subject.length() == 0 ? View.GONE : View.VISIBLE);

        ImageView avatarImg = helper.getView(R.id.item_simple_post_user_avatar);
        if (item.user_id == 0 && "匿名".equals(item.user_nick_name)) {
            GlideLoader4Common.simpleLoad(mContext, R.drawable.ic_anonymous, avatarImg);
        } else {
            GlideLoader4Common.simpleLoad(mContext, item.userAvatar, avatarImg);
        }

        NineGridLayout nineGridLayout = helper.getView(R.id.image_layout);
        if (!item.isLoadedImageData && SharePrefUtil.isShowImgAtTopicList(App.getContext())) {//没加载过
            nineGridLayout.setVisibility(View.GONE);
            RetrofitUtil
                    .getInstance()
                    .getApiService()
                    .getPostContent(1, 0, 0, item.topic_id, item.user_id,
                            SharePrefUtil.getToken(App.getContext()),
                            SharePrefUtil.getSecret(App.getContext()))
                    .enqueue(new Callback<PostDetailBean>() {
                        @Override
                        public void onResponse(@NonNull Call<PostDetailBean> call, @NonNull Response<PostDetailBean> response) {
                            if (response.body() != null && response.body().topic != null && response.body().topic.content != null) {
                                ArrayList<String> imgs = new ArrayList<>();
                                for (int i = 0; i < response.body().topic.content.size(); i ++) {
                                    if (response.body().topic.content.get(i).type ==  ContentDataType.TYPE_IMAGE) {
                                        imgs.add(response.body().topic.content.get(i).infor);
                                    }
                                }
                                item.imageUrls = imgs;
                                item.isLoadedImageData = true;
                                if (imgs.size() > 0) {
                                    nineGridLayout.setVisibility(View.VISIBLE);
                                    nineGridLayout.setNineGridAdapter(new NineImageAdapter(imgs));
                                } else {
                                    nineGridLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PostDetailBean> call, @NonNull Throwable t) {
                            nineGridLayout.setVisibility(View.GONE);
                        }
                    });
        } else {//加载过
            if (item.imageUrls != null && item.imageUrls.size() > 0) {
                nineGridLayout.setVisibility(View.VISIBLE);
                nineGridLayout.setNineGridAdapter(new NineImageAdapter(item.imageUrls));
            } else {
                nineGridLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setOnImgClickListener(OnImgClickListener onClickListener) {
        this.onImgClickListener = onClickListener;
    }

    public interface OnImgClickListener {
        void onImgClick(List<String> imgUrls, int selected);
    }
}
