package com.scatl.uestcbbs.module.user.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.Constant;

public class AtUserListAdapter extends BaseQuickAdapter<AtUserListBean.ListBean, BaseViewHolder> {
    public AtUserListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AtUserListBean.ListBean item) {
        helper.setText(R.id.item_at_user_list_name, item.name)
                .addOnClickListener(R.id.item_at_user_list_icon);
        String icon = Constant.USER_AVATAR_URL + item.uid;
        GlideLoader4Common.simpleLoad(mContext, icon, helper.getView(R.id.item_at_user_list_icon));
    }
}
