package com.scatl.uestcbbs.module.board.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.ForumDetailBean
import com.scatl.uestcbbs.entity.SubForumListBean

/**
 * Created by sca_tl at 2023/4/27 10:35
 */
interface BoardView: BaseView {
    fun onGetSubBoardListSuccess(subForumListBean: SubForumListBean)
    fun onGetSubBoardListError(msg: String?)
    fun onGetForumDetailSuccess(forumDetailBean: ForumDetailBean?)
}