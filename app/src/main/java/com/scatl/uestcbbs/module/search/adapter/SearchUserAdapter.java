package com.scatl.uestcbbs.module.search.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.util.TimeUtil;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 19:40
 */
public class SearchUserAdapter extends BaseQuickAdapter<SearchUserBean.BodyBean.ListBean, BaseViewHolder> {

    public SearchUserAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchUserBean.BodyBean.ListBean item) {
        helper.setText(R.id.search_user_name, item.name)
                .setText(R.id.search_user_last_login,
                        TimeUtil.formatTime(item.dateline, R.string.last_login_time, mContext));
        Glide.with(mContext).load(item.icon).into((ImageView) helper.getView(R.id.search_user_icon));

    }
}
