package com.scatl.uestcbbs.services

import android.app.Service
import android.content.Intent
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.entity.HeartMsgBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.MessageManager
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.subscribeEx
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import java.util.regex.Pattern
import kotlin.concurrent.thread

/**
 * Created by sca_tl at 2023/3/16 15:31
 */
class HeartMsgService: Service() {

    private var iAmGroot = true
    private var mCompositeDisposable: CompositeDisposable? = null

    companion object {
        const val SERVICE_NAME = "com.scatl.uestcbbs.services.HeartMsgService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mCompositeDisposable = CompositeDisposable()
        thread {
            while (iAmGroot) {
                try {
                    getMessageCount()
                    Thread.sleep(3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return START_STICKY
    }

    private fun getMessageCount() {
        val observable1 = RetrofitUtil
            .getInstance()
            .apiService
            .getHeartMsg(Constant.SDK_VERSION)
            .subscribeOn(Schedulers.io())

        val observable2 = RetrofitUtil
            .getInstance()
            .apiService
            .dianPingMsgCount
            .subscribeOn(Schedulers.io())

        val observable3 = RetrofitUtil
            .getInstance()
            .apiService
            .homeInfo
            .subscribeOn(Schedulers.io())

        val function = Function3<HeartMsgBean, String, String, HeartMsgBean> { p, s, t ->
            try {
                val elementsDianPing = Jsoup.parse(s).select("div[class=ct2_a wp cl]").select("ul[class=tb cl]").select("li")
                for (i in elementsDianPing.indices) {
                    if (elementsDianPing[i].text().contains("点评")) {
                        val matcher = Pattern.compile("点评\\((.*?)\\)").matcher(elementsDianPing[i].text())
                        if (matcher.matches()) {
                            if (p.body.dianPingBean == null) {
                                p.body.dianPingBean = HeartMsgBean.BodyBean.DianPingBean()
                            }
                            p.body.dianPingBean.count = matcher.group(1)?.toInt()?:0
                        }
                        break
                    }
                }

                val elementsHomeInfo = Jsoup.parse(t).select("div[class=bm bmw  flg cl]")
                for (i in elementsHomeInfo.indices) {
                    if (elementsHomeInfo[i].text().contains("我订阅的专辑")) {
                        elementsHomeInfo[i].select("div[class=bm_c]").select("td[class=fl_g]").forEach {
                            if (it.html().contains("forum_new")) {
                                val id = BBSLinkUtil.getLinkInfo(it.select("dl").select("dt").select("a").attr("href")).id
                                val collectionName = it.select("dl").select("dt").select("a").text()
                                if (p.body.collectionBeans == null) {
                                    p.body.collectionBeans = mutableListOf()
                                }
                                p.body.collectionBeans.add(HeartMsgBean.BodyBean.CollectionBean().apply {
                                    cid = id
                                    name = collectionName
                                })
                            }
                        }
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            p
        }

        Observable
            .zip(observable1, observable2, observable3, function)
            .subscribeEx(Observer<HeartMsgBean>().observer {
                onSuccess {
                    MessageManager.INSTANCE.pmUnreadCount = it.body?.pmInfos?.size?:0
                    MessageManager.INSTANCE.atUnreadCount = it.body?.atMeInfo?.count?:0
                    MessageManager.INSTANCE.replyUnreadCount = it.body?.replyInfo?.count?:0
                    MessageManager.INSTANCE.systemUnreadCount = it.body?.systemInfo?.count?:0
                    MessageManager.INSTANCE.dianPingUnreadCount = it.body?.dianPingBean?.count?:0
                    MessageManager.INSTANCE.collectionUpdateInfo = it.body?.collectionBeans?: mutableListOf()
                    //通知通知页面更新未读条数
                    EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
                }

                onError {

                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        iAmGroot = false
    }

    override fun onBind(intent: Intent?) = null
}