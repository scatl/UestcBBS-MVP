package com.scatl.uestcbbs.base

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by sca_tl on 2022/12/30 15:08
 */
open class BaseVBPresenter<V: BaseView> {

    companion object {
        const val TAG = "BaseVBPresenter"
    }

    var mView: V? = null
    var mCompositeDisposable: CompositeDisposable? = null

    fun attachView(view: V) {
        mView = view
        mCompositeDisposable = CompositeDisposable()
    }

    /**
     * 销毁视图时，取消已加入的网络请求
     */
    fun detachView() {
        mView = null
        mCompositeDisposable?.clear()
        mCompositeDisposable = null
    }

}