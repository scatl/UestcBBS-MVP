package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/9 16:15
 */
public interface SelectBoardView {
    void onGetBoardListSuccess(ForumListBean forumListBean);
    void onGetBoardListError(String msg);
    void onGetSubBoardListSuccess(SubForumListBean subForumListBean);
    void onGetSubBoardListError(String msg);
    void onGetSingleBoardDataSuccess(SingleBoardBean singleBoardBean);
    void onGetSingleBoardDataError(String msg);
    void onTagLayout1Select(int position);
    void onTagLayout2Select(int boardId, String boardName);
    void onTagLayout3Select(int boardId, String boardName);
    void onTagLayout4Select(int filterId, String filterName);
}
