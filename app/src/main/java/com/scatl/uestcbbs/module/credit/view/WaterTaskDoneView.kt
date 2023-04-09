package com.scatl.uestcbbs.module.credit.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.TaskBean

/**
 * created by sca_tl at 2023/4/7 19:37
 */
interface WaterTaskDoneView: BaseView {
    fun onGetDoneTaskSuccess(taskBeans: List<TaskBean>, formhash: String?)
    fun onGetDoneTaskError(msg: String?)
}