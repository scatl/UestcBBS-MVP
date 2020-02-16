package com.scatl.uestcbbs.module.board.view;

import com.scatl.uestcbbs.entity.ForumListBean;

public interface BoardListView {
    void onGetBoardListSuccess(ForumListBean forumListBean);
    void onGetBoardListError(String msg);
}
