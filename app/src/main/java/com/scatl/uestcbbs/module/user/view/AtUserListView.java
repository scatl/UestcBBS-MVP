package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.AtUserListBean;

public interface AtUserListView {
    void onGetAtUserListSuccess(AtUserListBean atUserListBean);
    void onGetAtUserListError(String msg);
}
