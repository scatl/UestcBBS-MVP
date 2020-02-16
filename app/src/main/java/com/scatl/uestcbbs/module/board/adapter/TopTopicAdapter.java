package com.scatl.uestcbbs.module.board.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SingleBoardBean;

public class TopTopicAdapter extends BaseQuickAdapter<SingleBoardBean.TopTopicListBean, BaseViewHolder> {
    public TopTopicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleBoardBean.TopTopicListBean item) {
        helper.setText(R.id.item_toptopic_title, item.title);
    }
}
