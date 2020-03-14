package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.PhotoListBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 14:06
 */
public interface UserPhotoView {
    void onGetUserPhotoSuccess(PhotoListBean photoListBean);
    void onGetUserPhotoError(String msg);
}
