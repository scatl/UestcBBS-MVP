package com.scatl.uestcbbs.module.setting.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.OpenSourceBean

/**
 * Created by sca_tl at 2023/6/6 16:16
 */
interface OpenSourceView: BaseView {
    fun onGetOpenSourceDataSuccess(data: List<OpenSourceBean>)
    fun onGetOpenSourceDataError(msg: String?)
}