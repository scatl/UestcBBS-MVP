package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.OnLineUserBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/10/7 11:25
 * description:
 */
public interface OnLineUserView {
    void onGetOnLineUserSuccess(OnLineUserBean onLineUserBean);
    void onGetOnLineUserError(String msg);
}
