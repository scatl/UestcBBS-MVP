package com.scatl.uestcbbs.module.board.view;

import com.scatl.uestcbbs.entity.SubForumListBean;

public interface BoardView {
    void onGetSubBoardListSuccess(SubForumListBean subForumListBean);
    void onGetSubBoardListError(String msg);
    void onSubBoardSelect(int position);
    void onFilterSelect(int fid, String name, int position);
}
