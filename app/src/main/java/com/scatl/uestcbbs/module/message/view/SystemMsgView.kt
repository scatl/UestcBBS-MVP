package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.SystemMsgBean

/**
 * Created by sca_tl at 2023/2/20 10:23
 */
interface SystemMsgView: BaseView {
    fun onGetSystemMsgSuccess(systemMsgBean: SystemMsgBean)
    fun onGetSystemMsgError(msg: String?)
}