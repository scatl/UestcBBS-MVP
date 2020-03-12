package com.scatl.uestcbbs.custom.postview.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ContentViewBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/11 17:48
 */
public class TextItemProvider extends BaseItemProvider<ContentViewBean, BaseViewHolder> {

    @Override
    public int viewType() {
        return PostContentMultiAdapter.TYPE_TEXT;
    }

    @Override
    public int layout() {
        return R.layout.view_content_view_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder helper, ContentViewBean data, int position) {
        helper.setText(R.id.view_content_view_text_content, data.infor);
    }
}
