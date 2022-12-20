package com.scatl.uestcbbs.module.post.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.image.ninelayout.NineGridLayout;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ContentDataType;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/12 17:43
 */
public class HotPostAdapter extends BaseQuickAdapter<HotPostBean.ListBean, BaseViewHolder> {

    private OnImgClickListener onImgClickListener;

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

        helper.getView(R.id.item_hot_post_content).setVisibility(item.summary == null || item.summary.length() == 0 ? View.GONE : View.VISIBLE);

        NineGridLayout nineGridLayout = helper.getView(R.id.image_layout);
        if (!item.isLoadedImageData && SharePrefUtil.isShowImgAtTopicList(App.getContext())) {//没加载过
            RetrofitUtil
                    .getInstance()
                    .getApiService()
                    .getPostContent(1, 0, 0, item.source_id, item.user_id,
                            SharePrefUtil.getToken(App.getContext()),
                            SharePrefUtil.getSecret(App.getContext()))
                    .enqueue(new Callback<PostDetailBean>() {
                        @Override
                        public void onResponse(@NonNull Call<PostDetailBean> call, @NonNull Response<PostDetailBean> response) {
                            if (response.body() != null && response.body().topic != null && response.body().topic.content != null) {
                                List<String> imgs = new ArrayList<>();
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
