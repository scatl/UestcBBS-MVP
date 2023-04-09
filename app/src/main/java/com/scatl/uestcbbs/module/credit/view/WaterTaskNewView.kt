package com.scatl.uestcbbs.module.credit.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.TaskBean

/**
 * Created by sca_tl at 2023/4/7 18:00
 */
interface WaterTaskNewView: BaseView {
    fun onGetNewTaskSuccess(taskBeans: List<TaskBean>, formhash: String?) { }
    fun onGetNewTaskError(msg: String?){ }
    fun onApplyNewTaskSuccess(msg: String?, taskId: Int, position: Int)
    fun onApplyNewTaskError(msg: String?)
}