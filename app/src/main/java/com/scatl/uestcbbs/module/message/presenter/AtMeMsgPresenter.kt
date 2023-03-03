package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.AtMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.AtMeMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/2/17 14:06
 */
class AtMeMsgPresenter: BaseVBPresenter<AtMeMsgView>() {

    private val messageModel = MessageModel()

    fun getAtMeMsg(page: Int, pageSize: Int) {
        messageModel.getAtMeMsg(page, pageSize,
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<AtMsgBean>() {
                override fun OnSuccess(atMsgBean: AtMsgBean) {
                    if (atMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetAtMeMsgSuccess(atMsgBean)
                    }
                    if (atMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetAtMeMsgError(atMsgBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetAtMeMsgError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

}