package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.ViewWarningEntity

/**
 * created by sca_tl at 2023/5/2 15:16
 */
interface ViewWarningView: BaseView {
    fun onGetWarningDataSuccess(entity: ViewWarningEntity)
    fun onGetWarningDataError(msg: String?)
}