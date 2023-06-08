package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.SystemMsgBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.SystemMsgView
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/20 10:23
 */
class SystemMsgPresenter: BaseVBPresenter<SystemMsgView>() {

    private val messageModel = MessageModel()

    fun getSystemMsg(page: Int, pageSize: Int) {
        messageModel
            .getSystemMsg(page, pageSize)
            .subscribeEx(Observer<SystemMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetSystemMsgSuccess(it)
                    }
                    if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetSystemMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetSystemMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}