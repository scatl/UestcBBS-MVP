package com.scatl.uestcbbs.module.home.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.GrabSofaBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/5/2 15:53
 * description:
 */
public class GrabSofaAdapter extends BaseQuickAdapter<GrabSofaBean.ChannelBean.ItemBean, BaseViewHolder> {
    public GrabSofaAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GrabSofaBean.ChannelBean.ItemBean item) {
        helper.setText(R.id.item_grab_sofa_board_name, item.category)
                .setText(R.id.item_grab_sofa_user_name, item.author)
                .setText(R.id.item_grab_sofa_content, item.description)
                .setText(R.id.item_grab_sofa_title, item.title)
                .setText(R.id.item_grab_sofa_time, item.pubDate);

        if (item.enclosure != null && item.enclosure.size() != 0) {
            helper.getView(R.id.item_grab_sofa_image).setVisibility(View.VISIBLE);
            GlideLoader4Common.simpleLoad(mContext, item.enclosure.get(0).url, helper.getView(R.id.item_grab_sofa_image));
        } else {
            helper.getView(R.id.item_grab_sofa_image).setVisibility(View.GONE);
        }

    }
}
