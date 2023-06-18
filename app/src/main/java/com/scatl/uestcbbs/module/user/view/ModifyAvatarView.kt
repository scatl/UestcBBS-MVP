package com.scatl.uestcbbs.module.user.view

import com.scatl.uestcbbs.base.BaseView

/**
 * Created by sca_tl at 2023/6/14 17:05
 */
interface ModifyAvatarView: BaseView {
    fun onGetParamsSuccess(agent: String?, input: String?)
    fun onGetParamsError(msg: String?)
    fun onUploadSuccess(msg: String?)
    fun onUploadError(msg: String?)
}