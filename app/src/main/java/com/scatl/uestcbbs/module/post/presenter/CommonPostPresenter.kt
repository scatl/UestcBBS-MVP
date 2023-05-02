package com.scatl.uestcbbs.module.post.presenter

import android.content.Context
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.SimplePostListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.CommonPostView
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/4/26 10:09
 */
class CommonPostPresenter: BaseVBPresenter<CommonPostView>() {

    val postModel = PostModel()

    fun userPost(page: Int, pageSize: Int, uid: Int, type: String?) {
        postModel.getUserPost(uid, page, pageSize, type,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(userPostBean: CommonPostBean) {
                    if (userPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(userPostBean)
                    }
                    if (userPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(userPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun getHotPostList(page: Int, pageSize: Int) {
        postModel.getHotPost(page, pageSize, 2,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(hotPostBean: CommonPostBean) {
                    if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(hotPostBean)
                    }
                    if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(hotPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun getHomeTopicList(page: Int, pageSize: Int, sortby: String?) {
        postModel.getHomeTopicList(page, pageSize, 0, sortby,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(hotPostBean: CommonPostBean) {
                    if (hotPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostSuccess(hotPostBean)
                    }
                    if (hotPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPostError(hotPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetPostError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}