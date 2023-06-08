package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.ReplyMeMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgView
import com.scatl.uestcbbs.util.subscribeEx
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/2/17 10:05
 */
class ReplyMeMsgPresenter: BaseVBPresenter<ReplyMeMsgView>() {

    private val messageModel = MessageModel()

    fun getReplyMeMsg(page: Int, pageSize: Int) {
        messageModel
            .getReplyMsg(page, pageSize)
            .subscribeEx(Observer<ReplyMeMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetReplyMeMsgSuccess(it)
                    }
                    if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetReplyMeMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetReplyMeMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}