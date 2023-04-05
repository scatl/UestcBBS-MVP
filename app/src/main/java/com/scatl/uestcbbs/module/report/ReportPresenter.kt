package com.scatl.uestcbbs.module.report

import android.content.Context
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BasePresenter
import com.scatl.uestcbbs.entity.ReportBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl on 2022/12/16 14:56
 */
class ReportPresenter: BasePresenter<ReportView>() {

    private val reportModel = ReportModel()

    fun report(idType: String, message: String, id: Int) {
        reportModel.report(idType, message, id,
            object : Observer<ReportBean>() {
                override fun OnSuccess(reportBean: ReportBean) {
                    if (reportBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onReportSuccess(reportBean)
                    }
                    if (reportBean.rs == ApiConstant.Code.ERROR_CODE) {
                        view.onReportError(reportBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onReportError(e.message)
                }

                override fun OnCompleted() { }

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

}