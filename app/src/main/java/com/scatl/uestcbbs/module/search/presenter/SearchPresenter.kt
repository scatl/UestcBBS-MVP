package com.scatl.uestcbbs.module.search.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.search.model.SearchModel
import com.scatl.uestcbbs.module.search.view.SearchView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/2/8 10:22
 */
class SearchPresenter: BaseVBPresenter<SearchView>() {

    private val searchModel = SearchModel()

    fun searchUser(page: Int, pageSize: Int, keyword: String?) {
        searchModel.searchUser(page, pageSize, 0, keyword,
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<SearchUserBean>() {
                override fun OnSuccess(searchUserBean: SearchUserBean) {
                    if (searchUserBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSearchUserSuccess(searchUserBean)
                    }
                    if (searchUserBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSearchUserError(searchUserBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSearchUserError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun searchPost(page: Int, pageSize: Int, keyword: String?) {
        searchModel.searchPost(page, pageSize, keyword,
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<SearchPostBean>() {
                override fun OnSuccess(searchPostBean: SearchPostBean) {
                    if (searchPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSearchPostSuccess(searchPostBean)
                    }
                    if (searchPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSearchPostError(searchPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSearchPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}