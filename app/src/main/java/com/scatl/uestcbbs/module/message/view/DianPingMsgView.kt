package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.DianPingMessageBean
import com.scatl.uestcbbs.entity.DianPingMsgBean

/**
 * Created by sca_tl at 2023/2/17 10:28
 */
interface DianPingMsgView: BaseView {
    fun onGetDianPingMsgSuccess(dianPingMessageBean: DianPingMsgBean)
    fun onGetDianPingMsgError(msg: String?)
}