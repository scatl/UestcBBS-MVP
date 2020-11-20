package com.scatl.uestcbbs.module.account.presenter;

import android.content.Context;
import android.text.Html;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.account.model.AccountModel;
import com.scatl.uestcbbs.module.account.view.AccountManagerView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Documented;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/5/16 21:30
 * description:
 */
public class AccountManagerPresenter extends BasePresenter<AccountManagerView> {

    AccountModel accountModel = new AccountModel();

    public void getRealNameInfo() {
        accountModel.getRealNameInfo(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

                if (s.contains("您必须")) {
                    view.onGetRealNameInfoError("请高级授权后查看实名关联信息");
                } else {
                    try {
                        Document document = Jsoup.parse(s);

                        String info = document.select("div[id=messagetext]").select("p").get(0).text();
                        view.onGetRealNameInfoError(info);

                    } catch (Exception e) {
                        view.onGetRealNameInfoError("查询实名关联信息失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetRealNameInfoError("查询实名关联信息失败：" + e.getMessage());
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }


    public void showHelpDialog(Context context) {

        String data = "无法显示帮助文档";
        InputStream is;
        try {
            is = context.getAssets().open("account_help.html");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toString();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("晓得了", null )
                .setMessage(Html.fromHtml(data))
                .create();
        dialog.show();
    }


}
