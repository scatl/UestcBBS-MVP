package com.scatl.uestcbbs.module.board.view;

import com.scatl.uestcbbs.entity.SubForumListBean;

public interface BoardView {
    void onGetSubBoardListSuccess(SubForumListBean subForumListBean);
    void onGetSubBoardListError(String msg);
    void onPermissionGranted(int action);
    void onPermissionRefused();
    void onPermissionRefusedWithNoMoreRequest();
}
