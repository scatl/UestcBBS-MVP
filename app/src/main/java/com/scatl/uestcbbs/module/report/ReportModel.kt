package com.scatl.uestcbbs.module.report

import com.scatl.uestcbbs.entity.ReportBean
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by tanlei02 on 2022/12/16 15:55
 */
class ReportModel {
    fun report(idType: String, message: String, id: Int, token: String, secret: String, observer: Observer<ReportBean>) {
        val observable = RetrofitUtil
            .getInstance()
            .apiService
            .report(idType, message, id, token, secret)
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}