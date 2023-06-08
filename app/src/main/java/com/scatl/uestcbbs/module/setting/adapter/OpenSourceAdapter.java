package com.scatl.uestcbbs.module.setting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.OpenSourceBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 13:46
 */
public class OpenSourceAdapter extends BaseQuickAdapter<OpenSourceBean, BaseViewHolder> {

    public OpenSourceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenSourceBean item) {
        helper.setText(R.id.item_open_source_name, (helper.getAdapterPosition() + 1) + "、" + item.name)
                .setText(R.id.item_open_source_author, "作者：" + item.author)
                .setText(R.id.item_open_source_desc, item.description);
    }
}
