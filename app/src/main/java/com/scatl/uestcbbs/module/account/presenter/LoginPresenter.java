package com.scatl.uestcbbs.module.account.presenter;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.account.model.LoginModel;
import com.scatl.uestcbbs.module.account.view.LoginView;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:04
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private LoginModel loginModel = new LoginModel();

    public void login(String userName, String userPsw) {
        loginModel.login(userName, userPsw, new Observer<LoginBean>() {
            @Override
            public void OnSuccess(LoginBean loginBean) {
                if (loginBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onLoginSuccess(loginBean);
                }

                if (loginBean.rs == ApiConstant.Code.ERROR_CODE) {
                    view.onLoginError(loginBean.head.errInfo);
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
                SubscriptionManager.getInstance().add(d);
            }
        });
    }

}
