package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/2 14:13
 */
public interface PostListView {
    void onGetHotPostSuccess(HotPostBean hotPostBean);
    void onGetHotPostError(String msg);
    void onGetSimplePostSuccess(SimplePostListBean simplePostListBean);
    void onGetSimplePostError(String msg);
    void onCleanCacheSuccess(String msg);
    void onCleanCacheError(String msg);
}
