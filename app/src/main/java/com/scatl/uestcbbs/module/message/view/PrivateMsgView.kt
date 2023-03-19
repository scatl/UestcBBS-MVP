package com.scatl.uestcbbs.module.message.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.PrivateMsgBean

/**
 * Created by sca_tl at 2023/3/16 11:31
 */
interface PrivateMsgView: BaseView {
    fun onGetPrivateMsgSuccess(privateMsgBean: PrivateMsgBean)
    fun onGetPrivateMsgError(msg: String?)
    fun onDeletePrivateMsgSuccess(msg: String?, position: Int)
    fun onDeletePrivateMsgError(msg: String?)
}