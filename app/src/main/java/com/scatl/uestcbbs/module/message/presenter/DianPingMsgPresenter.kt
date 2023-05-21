package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.DianPingMessageBean
import com.scatl.uestcbbs.entity.DianPingMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.DianPingMsgView
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.BBSLinkUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/2/17 10:29
 */
class DianPingMsgPresenter: BaseVBPresenter<DianPingMsgView>() {
    private val messageModel = MessageModel()

    fun getDianPingMsg(page: Int, pageSize: Int) {
        messageModel.getDianPingMsg(page, pageSize, object : Observer<DianPingMsgBean>() {
            override fun OnSuccess(t: DianPingMsgBean?) {
                if (t?.rs == ApiConstant.Code.SUCCESS_CODE) {
                    mView?.onGetDianPingMsgSuccess(t)
                }
                if (t?.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onGetDianPingMsgError(t.head?.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetDianPingMsgError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}