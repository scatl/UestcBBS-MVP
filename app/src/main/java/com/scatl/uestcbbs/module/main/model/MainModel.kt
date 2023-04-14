package com.scatl.uestcbbs.module.main.model

import com.scatl.uestcbbs.entity.SettingsBean
import com.scatl.uestcbbs.entity.UpdateBean
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sca_tl at 2023/4/11 17:20
 */
class MainModel {
    fun getUpdate(oldVersionCode: Int, isTest: Boolean): Observable<UpdateBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .getUpdateInfo(oldVersionCode, isTest)

    fun getSettings(): Observable<SettingsBean> =
         RetrofitUtil
            .getInstance()
            .apiService
            .settings

}