package com.scatl.uestcbbs.module.account.view;

/**
 * author: sca_tl
 * date: 2020/5/16 21:30
 * description:
 */
public interface AccountManagerView {
    void onGetRealNameInfoSuccess(String info);
    void onGetRealNameInfoError(String msg);
    void onGetUploadHashSuccess(String hash, String msg);
    void onGetUploadHashError(String msg);
}
