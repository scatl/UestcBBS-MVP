package com.scatl.uestcbbs.module.board.view;

import com.scatl.uestcbbs.entity.SingleBoardBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/30 14:59
 */
public interface SingleBoardView {
    void onGetSingleBoardDataSuccess(SingleBoardBean singleBoardBean);
    void onGetSingleBoardDataError(String msg);
    void onClassificationSelected(int filterId);
}
