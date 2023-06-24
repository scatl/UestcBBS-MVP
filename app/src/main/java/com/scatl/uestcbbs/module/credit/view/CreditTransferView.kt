package com.scatl.uestcbbs.module.credit.view

import com.scatl.uestcbbs.base.BaseView

/**
 * Created by sca_tl at 2023/6/20 14:11
 */
interface CreditTransferView: BaseView {
    fun onGetFormHashSuccess(formHash: String?)
    fun onGetFormHashError(msg: String?)
    fun onTransferSuccess(msg: String?)
    fun onTransferError(msg: String?)
}