package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.UserPostBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 12:27
 */
public interface SelfPostView {
    void onGetUserPostSuccess(UserPostBean userPostBean);
    void onGetUserPostError(String msg);
}
