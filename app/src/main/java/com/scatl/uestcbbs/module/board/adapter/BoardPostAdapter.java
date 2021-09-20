package com.scatl.uestcbbs.module.board.adapter;

import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ContentDataType;
import com.scatl.uestcbbs.custom.SBGASortableNinePhotoLayout;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardPostAdapter extends BaseQuickAdapter<SingleBoardBean.ListBean, BaseViewHolder> {

    private OnImgClickListener onImgClickListener;

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
        GlideLoader4Common.simpleLoad(mContext, item.userAvatar, helper.getView(R.id.item_simple_post_user_avatar));

        SBGASortableNinePhotoLayout sortableNinePhotoLayout = helper.getView(R.id.item_simple_post_img_bga_layout);
        if (!item.isLoadedImageData && SharePrefUtil.isShowImgAtTopicList(MyApplication.getContext())) {//没加载过
            RetrofitUtil
                    .getInstance()
                    .getApiService()
                    .getPostContent(1, 0, 0, item.topic_id, item.user_id,
                            SharePrefUtil.getToken(MyApplication.getContext()),
                            SharePrefUtil.getSecret(MyApplication.getContext()))
                    .enqueue(new Callback<PostDetailBean>() {
                        @Override
                        public void onResponse(Call<PostDetailBean> call, Response<PostDetailBean> response) {
                            if (response != null && response.body() != null && response.body().topic != null
                                    && response.body().topic.content != null) {
                                ArrayList<String> imgs = new ArrayList<>();
                                for (int i = 0; i < response.body().topic.content.size(); i ++) {
                                    if (response.body().topic.content.get(i).type ==  ContentDataType.TYPE_IMAGE) {
                                        imgs.add(response.body().topic.content.get(i).infor);
                                    }
                                }
                                item.imageUrls = imgs;
                                item.isLoadedImageData = true;
                                if (imgs != null && imgs.size() > 0) {
                                    sortableNinePhotoLayout.setVisibility(View.VISIBLE);
                                    sortableNinePhotoLayout.setData(imgs);
                                    sortableNinePhotoLayout.setDelegate((sortableNinePhotoLayout1, view, position, model, models) -> onImgClickListener.onImgClick(models, position));
                                } else {
                                    sortableNinePhotoLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PostDetailBean> call, Throwable t) {
                            sortableNinePhotoLayout.setVisibility(View.GONE);
                        }
                    });
        } else {//加载过
            if (item.imageUrls != null && item.imageUrls.size() > 0) {
                sortableNinePhotoLayout.setVisibility(View.VISIBLE);
                sortableNinePhotoLayout.setData((ArrayList<String>) item.imageUrls);
            } else {
                sortableNinePhotoLayout.setVisibility(View.GONE);
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


