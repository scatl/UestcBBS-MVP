package com.scatl.uestcbbs.module.board.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.entity.SingleBoardBean
import com.scatl.uestcbbs.entity.SubForumListBean

/**
 * Created by sca_tl at 2023/3/31 10:51
 */
interface SelectBoardView: BaseView {
    fun onGetMainBoardListSuccess(forumListBean: ForumListBean)
    fun onGetMainBoardListError(msg: String?)
    fun onGetChildBoardListSuccess(subForumListBean: SubForumListBean)
    fun onGetChildBoardListError(msg: String?)
    fun onGetClassificationSuccess(classifications: List<CommonPostBean.ClassificationTypeListBean>)
    fun onGetClassificationError(msg: String?, classifications: List<CommonPostBean.ClassificationTypeListBean>)
}