package com.scatl.uestcbbs.module.account.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.LoginBean

/**
 * Created by sca_tl at 2023/6/2 14:11
 */
interface LoginView: BaseView {
    fun onLoginSuccess(loginBean: LoginBean)
    fun onLoginError(msg: String?)
}