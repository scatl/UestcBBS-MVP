package com.scatl.uestcbbs.module.credit.view

import android.widget.TextView
import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.TaskBean

/**
 * Created by sca_tl at 2023/4/6 20:45
 */
interface WaterTaskDoingView: BaseView {
    fun onGetDoingTaskSuccess(taskBeans: List<TaskBean>, formhash: String?)
    fun onGetDoingTaskError(msg: String?)
    fun onDeleteDoingTaskSuccess(msg: String?, position: Int)
    fun onDeleteDoingTaskError(msg: String?)
    fun onGetTaskAwardSuccess(msg: String?, position: Int)
    fun onGetTaskAwardError(msg: String?)
    fun onCheckLeftTimeSuccess(leftTime: String?, textView: TextView?)
    fun onCheckLeftTimeError(msg: String?)
}