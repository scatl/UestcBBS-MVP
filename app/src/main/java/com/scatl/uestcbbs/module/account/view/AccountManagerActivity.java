package com.scatl.uestcbbs.module.account.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.module.account.adapter.AccountManagerAdapter;
import com.scatl.uestcbbs.services.heartmsg.view.HeartMsgService;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ServiceUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AccountManagerActivity extends BaseActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private Button addAccountBtn;
    private RecyclerView recyclerView;
    private AccountManagerAdapter accountManagerAdapter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_account_manager;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.account_manager_coor_lyout);
        toolbar = findViewById(R.id.account_manager_toolbar);
        addAccountBtn = findViewById(R.id.account_manager_add_account_btn);
        recyclerView = findViewById(R.id.account_manager_rv);
    }

    @Override
    protected void initView() {
        addAccountBtn.setOnClickListener(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountManagerAdapter = new AccountManagerAdapter(R.layout.item_account_manager);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(accountManagerAdapter);

        initAccountData();
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    /**
     * author: sca_tl
     * description:
     */
    private void initAccountData() {

        AccountBean a = new AccountBean();
        a.isLogin = SharePrefUtil.isLogin(this);
        a.userName = SharePrefUtil.getName(this);
        a.uid = SharePrefUtil.getUid(this);
        a.token = SharePrefUtil.getToken(this);
        a.secret = SharePrefUtil.getSecret(this);
        a.avatar = SharePrefUtil.getAvatar(this);

        List<AccountBean> list = LitePal
                .where("uid = ?", String.valueOf(a.uid))
                .find(AccountBean.class);
        if (list.size() == 0 && a.isLogin) a.save(); //当前已登录，但是数据库没有找到相关数据，则添加，兼容旧版本

        List<AccountBean> data = LitePal.findAll(AccountBean.class);
        accountManagerAdapter.setCurrentLoginUid(a.uid);
        accountManagerAdapter.setNewData(data);
    }

    @Override
    protected void setOnItemClickListener() {
        accountManagerAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_account_manager_layout) {
                AccountBean accountBean = new AccountBean();
                accountBean.isLogin = true;
                accountBean.avatar = accountManagerAdapter.getData().get(position).avatar;
                accountBean.secret = accountManagerAdapter.getData().get(position).secret;
                accountBean.token = accountManagerAdapter.getData().get(position).token;
                accountBean.uid = accountManagerAdapter.getData().get(position).uid;
                accountBean.userName = accountManagerAdapter.getData().get(position).userName;
                SharePrefUtil.setLogin(this, true, accountBean);

                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.LOGIN_SUCCESS));

                accountManagerAdapter.setCurrentLoginUid(accountBean.uid);
                accountManagerAdapter.notifyItemRangeChanged(0 , accountManagerAdapter.getData().size());
                accountBean.saveOrUpdate("uid = ?", String.valueOf(accountBean.uid));

                HeartMsgService.private_me_msg_count = 0;
                HeartMsgService.at_me_msg_count = 0;
                HeartMsgService.reply_me_msg_count = 0;

                //开启消息提醒服务
                if (! ServiceUtil.isServiceRunning(this, HeartMsgService.serviceName)) {
                    Intent intent1 = new Intent(this, HeartMsgService.class);
                    startService(intent1);
                }

                showSnackBar(coordinatorLayout, "欢迎回来，" + accountBean.userName);
            }
        });

        accountManagerAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_account_manager_delete_btn) {
                deleteAccountDialog(position);
            }
        });
    }

    @Override
    protected void onClickListener(View view) {

        if (view.getId() == R.id.account_manager_add_account_btn) {
            LoginFragment.getInstance(null)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void deleteAccountDialog(int position) {
        AccountBean accountBean = accountManagerAdapter.getData().get(position);

        String msg1 = "确认要删除帐号：" + accountBean.userName + " 吗？由于该帐号当前已登录，删除后会退出登录该账号";
        String msg2 = "确认要删除帐号：" + accountBean.userName + " 吗？";
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", null )
                .setTitle("删除帐号")
                .setMessage(accountBean.uid == SharePrefUtil.getUid(this) ? msg1 : msg2)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                dialog.dismiss();
                int i = LitePal.delete(AccountBean.class, accountBean.id);
                if (i != 0) {
                    HeartMsgService.private_me_msg_count = 0;
                    HeartMsgService.at_me_msg_count = 0;
                    HeartMsgService.reply_me_msg_count = 0;
                    accountManagerAdapter.getData().remove(position);
                    accountManagerAdapter.notifyItemRemoved(position);
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.LOGOUT_SUCCESS));
                    showSnackBar(coordinatorLayout, "删除成功");
                    if (accountBean.uid == SharePrefUtil.getUid(this)) SharePrefUtil.setLogin(this, false, new AccountBean());
                } else {
                    showSnackBar(coordinatorLayout, "删除失败，未找到该帐号");
                }

            });
        });
        dialog.show();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.ADD_ACCOUNT_SUCCESS) {
            LoginBean loginBean = (LoginBean) baseEvent.eventData;

            boolean sameAccount = false;
            List<AccountBean> data = LitePal.findAll(AccountBean.class);
            for (AccountBean a : data){
                if (a.uid == loginBean.uid) {
                    sameAccount = true;
                    break;
                }
            }
            if (!sameAccount) {
                AccountBean accountBean = new AccountBean();
                accountBean.isLogin = false;
                accountBean.avatar = loginBean.avatar;
                accountBean.secret = loginBean.secret;
                accountBean.token = loginBean.token;
                accountBean.uid = loginBean.uid;
                accountBean.userName = loginBean.userName;

                accountBean.save();
                accountManagerAdapter.addData(accountBean);
                accountManagerAdapter.notifyItemInserted(accountManagerAdapter.getData().size());
                showSnackBar(coordinatorLayout, "添加帐号成功");
            } else {
                showSnackBar(coordinatorLayout, "已有该帐号，点击即可登录");
            }
        }

    }

}

