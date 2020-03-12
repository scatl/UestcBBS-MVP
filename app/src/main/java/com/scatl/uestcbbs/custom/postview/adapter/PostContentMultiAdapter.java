package com.scatl.uestcbbs.custom.postview.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entity.ContentViewBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/11 17:32
 */
public class PostContentMultiAdapter extends MultipleItemRvAdapter<ContentViewBean, BaseViewHolder>{

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_LINK = 4;
    public static final int TYPE_ATTACHMENT = 5;
    public static final int TYPE_VOTE = 6;

    public PostContentMultiAdapter(@Nullable List<ContentViewBean> data) {
        super(data);
        finishInitialize();
    }

    @Override
    protected int getViewType(ContentViewBean o) {
        if (o.type == TYPE_TEXT) {
            return TYPE_TEXT;
        } else if (o.type == TYPE_IMAGE) {
            return TYPE_IMAGE;
        }

        return 0;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new TextItemProvider());
        mProviderDelegate.registerProvider(new ImageItemProvider());
    }
}
