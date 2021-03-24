package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.VisitorsBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/3/17 13:02
 * description:
 */
public interface UserMainPageView {
    void onGetUserSpaceSuccess(boolean isOnline, String onLineTime, String registerTime, String lastLoginTime, String ipLocation);
    void onGetUserSpaceError(String msg);
}
