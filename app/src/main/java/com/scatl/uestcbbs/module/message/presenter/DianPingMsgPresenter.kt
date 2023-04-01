package com.scatl.uestcbbs.module.message.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.DianPingMessageBean
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

    fun getDianPingMsg(page: Int) {
        messageModel.getDianPingMsg(page, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val elements = document.select("div[class=ct2_a wp cl]").select("div[class=xld xlda]").select("div[class=nts]").select("dl")
                    val dianPingMessageBeans: MutableList<DianPingMessageBean> = ArrayList()
                    for (i in elements.indices) {
                        val d = DianPingMessageBean()
                        d.time = elements[i].select("span[class=xg1 xw0]").text()
                        d.userName = elements[i].select("dd[class=ntc_body]").select("a")[0].text()
                        d.uid = BBSLinkUtil.getLinkInfo(elements[i].select("dd[class=ntc_body]").select("a")[0].attr("href")).id
                        d.userAvatar = Constant.USER_AVATAR_URL + d.uid
                        d.tid = BBSLinkUtil.getLinkInfo(elements[i].select("dd[class=ntc_body]").select("a")[1].attr("href")).id
                        d.topicTitle = elements[i].select("dd[class=ntc_body]").select("a")[1].text()
                        d.pid = BBSLinkUtil.getLinkInfo(elements[i].select("dd[class=ntc_body]").select("a")[2].attr("href")).pid
                        dianPingMessageBeans.add(d)
                    }
                    mView?.onGetDianPingMessageSuccess(dianPingMessageBeans, s.contains("下一页"))
                } catch (e: Exception) {
                    mView?.onGetDianPingMessageError("获取点评消息失败：" + e.message)
                    e.printStackTrace()
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetDianPingMessageError("获取点评消息失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}