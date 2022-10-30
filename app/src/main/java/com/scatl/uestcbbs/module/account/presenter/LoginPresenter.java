package com.scatl.uestcbbs.module.account.presenter;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.account.model.AccountModel;
import com.scatl.uestcbbs.module.account.view.LoginView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.io.IOException;
import java.util.HashSet;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:04
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private AccountModel accountModel = new AccountModel();

    public void login(Context context, String userName, String userPsw) {
        accountModel.login(userName, userPsw, new Observer<Response<ResponseBody>>() {
            @Override
            public void OnSuccess(Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String res = response.body().string();
                        LoginBean loginBean = JSONObject.parseObject(res, LoginBean.class);
                        if (loginBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onLoginSuccess(loginBean);
                        }else {
                            view.onLoginError(loginBean.head.errInfo);
                        }
                    }
                    HashSet<String> cookies = new HashSet<>(response.headers().values("Set-Cookie"));
                    SharePrefUtil.setCookies(context, cookies, userName);
                    SharePrefUtil.setSuperAccount(context, true, userName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onLoginError(e.message);
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

    public void showLoginReasonDialog(Context context, int selected) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("安全提问")
                .setSingleChoiceItems(R.array.login_question, selected, (dialog1, which) -> {
                    view.onLoginReasonSelected(which);
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

}
