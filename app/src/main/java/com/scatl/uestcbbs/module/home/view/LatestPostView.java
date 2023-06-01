package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.NoticeBean;

public interface LatestPostView {
    void getBannerDataSuccess(BingPicBean bingPicBean);
    void getSimplePostDataSuccess(CommonPostBean simplePostListBean);
    void getSimplePostDataError(String msg);
    void onGetNoticeSuccess(NoticeBean noticeBean);
    void onGetNoticeError(String msg);
    void onGetHomePageSuccess(String msg);
}
