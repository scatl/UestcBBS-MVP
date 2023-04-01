package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.AtMsgBean

/**
 * Created by sca_tl at 2023/2/17 14:06
 */
interface AtMeMsgView: BaseView {
    fun onGetAtMeMsgSuccess(atMsgBean: AtMsgBean)
    fun onGetAtMeMsgError(msg: String?)
}