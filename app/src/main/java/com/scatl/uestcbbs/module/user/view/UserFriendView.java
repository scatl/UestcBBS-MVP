package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.UserFriendBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/5 16:42
 */
public interface UserFriendView {
    void onGetUserFriendSuccess(UserFriendBean userFriendBean);
    void onGetUserFriendError(String msg);
}
