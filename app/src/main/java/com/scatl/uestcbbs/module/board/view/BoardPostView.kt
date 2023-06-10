package com.scatl.uestcbbs.module.board.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.CommonPostBean

/**
 * created by sca_tl at 2023/6/10 12:06
 */
interface BoardPostView: BaseView {
    fun onGetBoardPostSuccess(commonPostBean: CommonPostBean)
    fun onGetBoardPostError(msg: String?)
    fun onPaySuccess(msg: String?)
    fun onPayError(msg: String?)
}