package com.scatl.uestcbbs.module.search.model

import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sca_tl at 2023/4/4 9:52
 */
class SearchModel {
    fun searchUser(page: Int,
                   pageSize: Int,
                   searchId: Int,
                   keyword: String?): Observable<SearchUserBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .searchUser(page, pageSize, searchId, keyword)

    fun searchPost(page: Int,
                   pageSize: Int,
                   keyword: String?): Observable<SearchPostBean> =
        RetrofitUtil
            .getInstance()
            .apiService
            .searchPost(page, pageSize, keyword)

}