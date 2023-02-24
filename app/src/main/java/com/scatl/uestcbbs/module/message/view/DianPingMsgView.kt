package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.DianPingMessageBean

/**
 * Created by tanlei02 at 2023/2/17 10:28
 */
interface DianPingMsgView: BaseView {
    fun onGetDianPingMessageSuccess(dianPingMessageBean: List<DianPingMessageBean>, hasNext: Boolean)
    fun onGetDianPingMessageError(msg: String?)
}