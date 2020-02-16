package com.scatl.uestcbbs.module.account.adapter;

import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AccountBean;

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
        helper.addOnClickListener(R.id.item_account_manager_delete_btn);
        RadioButton radioButton = helper.getView(R.id.item_account_manager_radiobtn);
        radioButton.setText(currentLoginUid == item.uid ? item.userName + "（已登录）" : item.userName);
        radioButton.setChecked(currentLoginUid == item.uid);
    }
}
