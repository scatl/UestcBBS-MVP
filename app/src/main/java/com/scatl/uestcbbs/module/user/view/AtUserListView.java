package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.base.BaseView;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.SearchUserBean;

public interface AtUserListView extends BaseView {
    void onGetAtUserListSuccess(AtUserListBean atUserListBean);
    void onGetAtUserListError(String msg);
    void onSearchUserSuccess(SearchUserBean searchUserBean);
    void onSearchUserError(String msg);
}
