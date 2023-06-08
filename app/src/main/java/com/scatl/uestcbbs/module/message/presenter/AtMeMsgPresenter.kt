package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.http.BaseBBSResponseBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.AtMeMsgView
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/2/17 14:06
 */
class AtMeMsgPresenter: BaseVBPresenter<AtMeMsgView>() {

    private val messageModel = MessageModel()

    fun getAtMeMsg(page: Int, pageSize: Int) {
        messageModel
            .getAtMeMsg(page, pageSize)
            .subscribeEx(Observer<AtMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetAtMeMsgSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetAtMeMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetAtMeMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}