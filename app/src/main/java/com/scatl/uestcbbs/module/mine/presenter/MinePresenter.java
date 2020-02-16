package com.scatl.uestcbbs.module.mine.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.module.mine.model.MineModel;
import com.scatl.uestcbbs.module.mine.view.MineView;
import com.scatl.uestcbbs.util.SharePrefUtil;

public class MinePresenter extends BasePresenter<MineView> {

    private MineModel mineModel = new MineModel();

    public void logout(Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("退出登录")
                .setMessage("确认要退出登录吗？")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                SharePrefUtil.setLogin(context, false, new AccountBean());
                view.onLoginOutSuccess();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
