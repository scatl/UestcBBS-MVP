package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.AlbumListBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/14 13:06
 */
public interface UserAlbumView {
    void onGetAlbumListSuccess(AlbumListBean albumListBean);
    void onGetAlbumListError(String msg);
}
