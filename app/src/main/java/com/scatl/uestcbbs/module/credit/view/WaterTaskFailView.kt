package com.scatl.uestcbbs.module.credit.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.TaskBean

/**
 * created by sca_tl at 2023/4/7 20:29
 */
interface WaterTaskFailView: WaterTaskNewView {
    fun onGetFailedTaskSuccess(taskBeans: List<TaskBean>, formhash: String?)
    fun onGetFailedTaskError(msg: String?)
}