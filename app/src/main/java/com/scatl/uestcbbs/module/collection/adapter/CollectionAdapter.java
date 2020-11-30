package com.scatl.uestcbbs.module.collection.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.CollectionDetailBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.ForumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/7 16:18
 * description:
 */
public class CollectionAdapter extends BaseQuickAdapter<CollectionDetailBean.PostListBean, BaseViewHolder> {
    public CollectionAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addData(List<CollectionDetailBean.PostListBean> data, boolean refresh) {
        List<CollectionDetailBean.PostListBean> newList = new ArrayList<>();

        for (int i = 0; i <data.size(); i ++) {
            if (!ForumUtil.isInBlackList(data.get(i).authorId)) {
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
    protected void convert(@NonNull BaseViewHolder helper, CollectionDetailBean.PostListBean item) {
        helper.setText(R.id.item_collection_post_title, item.topicTitle)
                .setText(R.id.item_collection_time, item.postDate + "发表，" + item.lastPostDate + "有回复")
                .setText(R.id.item_collection_user_name, item.authorName)
                .setText(R.id.item_collection_comments_count, " " + item.commentCount)
                .setText(R.id.item_collection_view_count, " " + item.viewCount)
                .addOnClickListener(R.id.item_collection_user_avatar);
        GlideLoader4Common.simpleLoad(mContext, item.authorAvatar, helper.getView(R.id.item_collection_user_avatar));
    }
}
