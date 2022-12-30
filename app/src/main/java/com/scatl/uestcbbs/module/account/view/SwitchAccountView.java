package com.scatl.uestcbbs.module.account.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.module.account.adapter.AccountSwitchAdapter;

import org.litepal.LitePal;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/9/20 10:01
 * description: 切换帐户界面，不同于AccountManagerActivity,该view主要用于临时切换帐户，例如可用于发表评论时
 */
public class SwitchAccountView extends LinearLayout {

    private RecyclerView recyclerView;
    private int currentSelectUid;
    private AccountSwitchAdapter accountSwitchAdapter;

    private OnItemClickListener mOnItemClickListener;

    public SwitchAccountView(Context context) {
        super(context);
        init();
    }

    public SwitchAccountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchAccountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwitchAccountView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_account_switch, new LinearLayout(getContext()), true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        recyclerView = rootView.findViewById(R.id.layout_account_switch_rv);
        addView(rootView, layoutParams);

        accountSwitchAdapter = new AccountSwitchAdapter(R.layout.item_account_switch);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(getContext()));
        recyclerView.setAdapter(accountSwitchAdapter);

        accountSwitchAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_account_switch_layout) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(accountSwitchAdapter.getData().get(position).uid);
                }
            }
        });
    }

    public void setCurrentSelect(int uid) {
        this.currentSelectUid = uid;

        List<AccountBean> data = LitePal.findAll(AccountBean.class);
        accountSwitchAdapter.setCurrentSelectUid(currentSelectUid);
        accountSwitchAdapter.setNewData(data);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int selectUid);
    }

}
