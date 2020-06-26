package com.scatl.uestcbbs.module.account.view;

import com.scatl.uestcbbs.entity.LoginBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:04
 */
public interface LoginView {
    void onSimpleLoginSuccess(LoginBean loginBean);
    void onSimpleLoginError(String msg);

    void onGetCookiesSuccess(String msg);
    void onGetCookiesError(String msg);

    void onGetUploadHashSuccess(String hash, String msg);
    void onGetUploadHashError(String msg);
}
