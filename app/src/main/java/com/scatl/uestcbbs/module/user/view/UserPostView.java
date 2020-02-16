package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.UserPostBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:21
 */
public interface UserPostView {
    void onGetUserPostSuccess(UserPostBean userPostBean);
    void onGetUserPostError(String msg);
}
