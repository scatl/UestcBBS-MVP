package com.scatl.uestcbbs.module.report

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.ReportBean

/**
 * Created by sca_tl on 2022/12/16 14:56
 */
interface ReportView: BaseView {
    fun onReportSuccess(reportBean: ReportBean)
    fun onReportError(msg: String?)
}