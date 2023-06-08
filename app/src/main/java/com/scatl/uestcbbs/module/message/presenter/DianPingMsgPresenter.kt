package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.DianPingMsgBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.DianPingMsgView
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/17 10:29
 */
class DianPingMsgPresenter: BaseVBPresenter<DianPingMsgView>() {
    private val messageModel = MessageModel()

    fun getDianPingMsg(page: Int, pageSize: Int) {
        messageModel
            .getDianPingMsg(page, pageSize)
            .subscribeEx(Observer<DianPingMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetDianPingMsgSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetDianPingMsgError(it.head?.errInfo)
                    }
                }

                onError {
                    mView?.onGetDianPingMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}