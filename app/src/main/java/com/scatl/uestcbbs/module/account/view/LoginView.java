package com.scatl.uestcbbs.module.account.view;

import com.scatl.uestcbbs.entity.LoginBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:04
 */
public interface LoginView {
    void onLoginSuccess(LoginBean loginBean);
    void onLoginError(String msg);

    void onLoginReasonSelected(int position);
}
