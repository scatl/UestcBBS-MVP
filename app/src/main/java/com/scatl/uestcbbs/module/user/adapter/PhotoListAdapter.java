package com.scatl.uestcbbs.module.user.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 14:14
 */
public class PhotoListAdapter extends BaseQuickAdapter<PhotoListBean.ListBean, BaseViewHolder> {

    public PhotoListAdapter(int layoutResId) {
        super(layoutResId);
    }

    public List<String> getImgUrls() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < getData().size(); i ++) {
            list.add(getData().get(i).thumb_pic);
        }
        return list;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PhotoListBean.ListBean item) {
        GlideLoader4Common.simpleLoad(mContext, item.thumb_pic, helper.getView(R.id.item_user_photo_image));
    }
}
