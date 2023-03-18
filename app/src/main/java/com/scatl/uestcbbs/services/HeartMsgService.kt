package com.scatl.uestcbbs.services

import android.app.Service
import android.content.Intent
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.entity.HeartMsgBean
import com.scatl.uestcbbs.module.message.MessageManager
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.RetrofitCookieUtil
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import kotlin.concurrent.thread

/**
 * Created by tanlei02 at 2023/3/16 15:31
 */
class HeartMsgService: Service() {

    private var iAmGroot = true

    companion object {
        const val SERVICE_NAME = "com.scatl.uestcbbs.services.HeartMsgService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread {
            while (iAmGroot) {
                try {
                    getHeartMsg()
                    getDianPingMsg()

                    Thread.sleep(7000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return START_STICKY
    }

    private fun getHeartMsg() {
        RetrofitUtil
            .getInstance()
            .apiService
            .getHeartMsg(Constant.SDK_VERSION, SharePrefUtil.getToken(this), SharePrefUtil.getSecret(this))
            .enqueue(object : Callback<HeartMsgBean?> {
                override fun onResponse(call: Call<HeartMsgBean?>, response: Response<HeartMsgBean?>) {
                    if (response.body() != null) {
                        try {
                            MessageManager.INSTANCE.pmUnreadCount = response.body()?.body?.pmInfos?.size?:0
                            MessageManager.INSTANCE.atUnreadCount = response.body()?.body?.atMeInfo?.count?:0
                            MessageManager.INSTANCE.replyUnreadCount = response.body()?.body?.replyInfo?.count?:0
                            MessageManager.INSTANCE.systemUnreadCount = response.body()?.body?.systemInfo?.count?:0
                            //通知通知页面更新未读条数
                            EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<HeartMsgBean?>, t: Throwable) { }
            })
    }

    private fun getDianPingMsg() {
        RetrofitCookieUtil
            .getInstance()
            .apiService
            .dianPingMsg
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        val document = Jsoup.parse(response.body())
                        val elements = document.select("div[class=ct2_a wp cl]").select("ul[class=tb cl]").select("li")
                        for (i in elements.indices) {
                            if (elements[i].text().contains("点评")) {
                                val matcher = Pattern.compile("点评\\((.*?)\\)").matcher(elements[i].text())
                                if (matcher.matches()) {
                                    MessageManager.INSTANCE.systemUnreadCount = matcher.group(1)?.toInt()?:0
                                    EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
                                }
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        iAmGroot = false
    }

    override fun onBind(intent: Intent?) = null
}