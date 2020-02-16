package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;

public interface HomeView {
    void getBannerDataSuccess(BingPicBean bingPicBean);
    void getSimplePostDataSuccess(SimplePostListBean simplePostListBean);
    void getSimplePostDataError(String msg);
    void onPermissionGranted(int action);
    void onPermissionRefused();
    void onPermissionRefusedWithNoMoreRequest();
}
