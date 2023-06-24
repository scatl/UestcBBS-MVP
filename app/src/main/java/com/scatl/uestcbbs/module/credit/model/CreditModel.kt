package com.scatl.uestcbbs.module.credit.model

import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by RetrofitUtil at 2023/6/20 14:32
 */
class CreditModel {

    fun getCreditHistory(page: Int, creditType: Int, inOrOut: Int): Observable<String> {
        val map = hashMapOf(
            "exttype"       to      creditType.toString(),
            "starttime"     to      "",
            "endtime"       to      "",
            "income"        to      inOrOut.toString(),
            "optype"        to      "",
            "search"        to      "true",
            "op"            to      "log",
            "ac"            to      "credit",
            "mod"           to      "spacecp",
        )
        
        return RetrofitUtil
            .getInstance()
            .apiService
            .getCreditHistory(page, RetrofitUtil.generateRequestBody(map))
    }

    fun getFormHash(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .homeInfo
    }

    fun creditTransfer(formHash: String, amount: String, happyBoy: String, password: String, message: String): Observable<String> {
        val map = hashMapOf(
            "formhash"              to      formHash,
            "transfersubmit"        to      "true",
            "handlekey"             to      "transfercredit",
            "transferamount"        to      amount,
            "to"                    to      happyBoy,
            "password"              to      password,
            "transfermessage"       to      message,
            "transfersubmit_btn"    to      "true",
            "mod"                   to      "spacecp",
        )
        
        return RetrofitUtil
            .getInstance()
            .apiService
            .creditTransfer(RetrofitUtil.generateRequestBody(map))
    }

    fun getNewTask(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .newTask
    }

    fun getDoingTask(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .doingTask
    }

    fun getDoneTask(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .doneTask
    }

    fun getFailedTask(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .failedTask
    }

    fun getTaskDetail(id: Int): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .getTaskDetail(id)
    }

    fun applyNewTask(id: Int): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .applyNewTask(id)
    }

    fun getTaskAward(id: Int): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .getTaskAward(id)
    }

    fun deleteDoingTask(id: Int, formhash: String?): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .deleteDoingTask(id, formhash)
    }
}