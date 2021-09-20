package com.scatl.uestcbbs.module.account.adapter;

import android.view.View;
import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.SharePrefUtil;

/**
 * author: sca_tl
 * date: 2021/9/20 10:12
 * description:
 */
public class AccountSwitchAdapter extends BaseQuickAdapter<AccountBean, BaseViewHolder> {

    private int currentSelectUid;

    public AccountSwitchAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setCurrentSelectUid(int currentSelectUid) {
        this.currentSelectUid = currentSelectUid;
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountBean item) {
        helper.setText(R.id.item_account_switch_name, item.userName)
                .setText(R.id.item_account_switch_account_status, SharePrefUtil.isSuperLogin(mContext, item.userName) ? "已高级授权" : "未高级授权");
        RadioButton radioButton = helper.getView(R.id.item_account_switch_radiobtn);
        radioButton.setChecked(currentSelectUid == item.uid);
        GlideLoader4Common.simpleLoad(mContext, item.avatar, helper.getView(R.id.item_account_switch_avatar));
    }
}
