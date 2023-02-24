package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.ReplyMeMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by tanlei02 at 2023/2/17 10:05
 */
class ReplyMeMsgPresenter: BaseVBPresenter<ReplyMeMsgView>() {

    private val messageModel = MessageModel()

    fun getReplyMeMsg(page: Int, pageSize: Int) {
        messageModel.getReplyMsg(page, pageSize, SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<ReplyMeMsgBean>() {
                override fun OnSuccess(replyMeMsgBean: ReplyMeMsgBean) {
                    if (replyMeMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetReplyMeMsgSuccess(replyMeMsgBean)
                    }
                    if (replyMeMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetReplyMeMsgError(replyMeMsgBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetReplyMeMsgError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}