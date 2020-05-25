package com.scatl.uestcbbs.module.account.adapter;

import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.util.SharePrefUtil;

public class AccountManagerAdapter extends BaseQuickAdapter<AccountBean, BaseViewHolder> {

    private int currentLoginUid;

    public AccountManagerAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setCurrentLoginUid(int currentLoginUid) {
        this.currentLoginUid = currentLoginUid;
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountBean item) {
        helper.setText(R.id.item_account_manager_name, item.userName)
                .setText(R.id.item_account_manager_super_account_status, SharePrefUtil.isSuperLogin(mContext, item.userName) ? "已高级授权" : "未高级授权")
                .addOnClickListener(R.id.item_account_manager_super_login_btn)
                .addOnClickListener(R.id.item_account_manager_delete_btn);
        RadioButton radioButton = helper.getView(R.id.item_account_manager_radiobtn);
//        radioButton.setText(SharePrefUtil.isSuperAccount(mContext, item.userName) ? item.userName + "（已高级授权）" : item.userName + "未高级授权");
        radioButton.setChecked(currentLoginUid == item.uid);
        GlideLoader4Common.simpleLoad(mContext, item.avatar, helper.getView(R.id.item_account_manager_avatar));
    }
}
