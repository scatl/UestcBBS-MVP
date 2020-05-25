package com.scatl.uestcbbs.module.mine.view;

import com.scatl.uestcbbs.entity.UserGroupBean;

public interface MineView {
    void onLoginOutSuccess();
    void onGetUserGroupSuccess(UserGroupBean userGroupBean);
    void onGetUserGroupError(String msg);
}
