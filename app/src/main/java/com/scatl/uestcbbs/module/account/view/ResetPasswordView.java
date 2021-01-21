package com.scatl.uestcbbs.module.account.view;

public interface ResetPasswordView {
    void onFindUserNameSuccess(String msg);
    void onFindUserNameError(String msg);
    void onResetPswSuccess(String msg);
    void onResetPswError(String msg);
    void onGetFormHashSuccess(String formHash);
    void onGetFormHashError(String msg);
}
