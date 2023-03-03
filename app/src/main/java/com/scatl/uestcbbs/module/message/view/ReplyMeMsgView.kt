package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.ReplyMeMsgBean

/**
 * Created by sca_tl at 2023/2/17 10:05
 */
interface ReplyMeMsgView: BaseView {
    fun onGetReplyMeMsgSuccess(replyMeMsgBean: ReplyMeMsgBean)
    fun onGetReplyMeMsgError(msg: String?)
}