package com.scatl.uestcbbs.module.account.view

import com.scatl.uestcbbs.base.BaseView

/**
 * Created by sca_tl at 2023/6/2 9:42
 */
interface AccountManageView: BaseView {
    fun onGetRealNameInfoSuccess(info: String?)
    fun onGetRealNameInfoError(msg: String?)
    fun onGetUploadHashSuccess(hash: String?, msg: String?, toast: Boolean)
    fun onGetUploadHashError(msg: String?, toast: Boolean)
}