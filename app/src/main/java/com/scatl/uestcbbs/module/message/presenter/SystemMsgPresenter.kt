package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.SystemMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/2/20 10:23
 */
class SystemMsgPresenter: BaseVBPresenter<SystemMsgView>() {

    private val messageModel = MessageModel()

    fun getSystemMsg(page: Int, pageSize: Int) {
        messageModel.getSystemMsg(page, pageSize,
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<SystemMsgBean>() {
                override fun OnSuccess(systemMsgBean: SystemMsgBean) {
                    if (systemMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetSystemMsgSuccess(systemMsgBean)
                    }
                    if (systemMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetSystemMsgError(systemMsgBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetSystemMsgError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}