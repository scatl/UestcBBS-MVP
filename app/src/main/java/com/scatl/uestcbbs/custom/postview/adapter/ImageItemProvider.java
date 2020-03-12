package com.scatl.uestcbbs.custom.postview.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.RoundImageView;
import com.scatl.uestcbbs.entity.ContentViewBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/11 19:41
 */
public class ImageItemProvider extends BaseItemProvider<ContentViewBean, BaseViewHolder> {
    @Override
    public int viewType() {
        return PostContentMultiAdapter.TYPE_IMAGE;
    }

    @Override
    public int layout() {
        return R.layout.view_content_view_imageview;
    }

    @Override
    public void convert(@NonNull BaseViewHolder helper, ContentViewBean data, int position) {
        Glide.with(mContext)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.img_loading_img)
                        .error(R.drawable.img_loading_img))
                .load(data.infor)
                .into((RoundImageView)helper.getView(R.id.post_detail_custom_imageView));
        //(helper.getView(R.id.post_detail_custom_imageView)).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
