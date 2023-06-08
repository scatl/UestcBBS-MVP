package com.scatl.uestcbbs.module.search.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.search.model.SearchModel
import com.scatl.uestcbbs.module.search.view.SearchView
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/8 10:22
 */
class SearchPresenter: BaseVBPresenter<SearchView>() {

    private val searchModel = SearchModel()

    fun searchUser(page: Int, pageSize: Int, keyword: String?) {
        searchModel
            .searchUser(page, pageSize, 0, keyword)
            .subscribeEx(Observer<SearchUserBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSearchUserSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSearchUserError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onSearchUserError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun searchPost(page: Int, pageSize: Int, keyword: String?) {
        searchModel
            .searchPost(page, pageSize, keyword)
            .subscribeEx(Observer<SearchPostBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSearchPostSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSearchPostError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onSearchPostError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}