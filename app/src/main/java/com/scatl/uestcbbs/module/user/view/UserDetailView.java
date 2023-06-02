package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.VisitorsBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/3 13:08
 */
public interface UserDetailView {
    void onGetUserDetailSuccess(UserDetailBean userDetailBean);
    void onGetUserDetailError(String msg);
    void onFollowUserSuccess(FollowUserBean followUserBean);
    void onFollowUserError(String msg);
    void onBlackUserSuccess(BlackUserBean blackUserBean);
    void onBlackUserError(String msg);
    void onModifySignSuccess(ModifySignBean modifySignBean, String sign);
    void onModifySignError(String msg);
    void onModifyPswSuccess(ModifyPswBean modifyPswBean);
    void onModifyPswError(String msg);
    void onGetUserSpaceSuccess(List<VisitorsBean> visitorsBeans, List<String> medalImages);
    void onGetUserSpaceError(String msg);
    void onGetUserFriendSuccess(UserFriendBean userFriendBean);
    void onGetUserFriendError(String msg);
    void onGetSpaceByNameSuccess(int uid);
    void onGetSpaceByNameError(String msg);
}
