package com.scatl.uestcbbs.module.account.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.account.model.LoginModel;
import com.scatl.uestcbbs.module.account.view.LoginView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Response;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:04
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private LoginModel loginModel = new LoginModel();

    public void simpleLogin(String userName, String userPsw) {
        loginModel.simpleLogin(userName, userPsw, new Observer<LoginBean>() {
            @Override
            public void OnSuccess(LoginBean loginBean) {
                if (loginBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onSimpleLoginSuccess(loginBean);
                }

                if (loginBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onSimpleLoginError(loginBean.head.errInfo);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onSimpleLoginError(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                SubscriptionManager.getInstance().add(d);
            }
        });
    }

    public void superLogin(Context context, String userName, String userPsw) {
        loginModel.loginForCookies(userName, userPsw, new Observer<Response<ResponseBody>>() {
            @Override
            public void OnSuccess(Response<ResponseBody> responseBodyResponse) {

                try {
                    ResponseBody responseBody = responseBodyResponse.body();
                    BufferedSource source;
                    if (responseBody != null) {
                        source = responseBody.source();
                        source.request(Long.MAX_VALUE);
                        Buffer buffer = source.getBuffer();
                        String responseBodyString = buffer.clone().readString(StandardCharsets.UTF_8);

                        //登录成功
                        if (responseBodyString.contains("欢迎") && responseBodyString.contains(userName)) {
                            HashSet<String> cookies = new HashSet<>(responseBodyResponse.headers().values("Set-Cookie"));
                            SharePrefUtil.setCookies(context, cookies, userName);
                            SharePrefUtil.setSuperAccount(context, true, userName);
                            view.onSuperLoginSuccess("授权成功！");

                        } else {
                            view.onSuperLoginError("授权失败，请检查用户名、密码是否正确");
                        }

                    } else {
                        view.onSuperLoginError("授权失败：响应为空");
                    }

                } catch (Exception e) {
                    view.onSuperLoginError("授权失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onSuperLoginError("授权失败:" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
//                SubscriptionManager.getInstance().add(d);
            }
        });
    }

}
