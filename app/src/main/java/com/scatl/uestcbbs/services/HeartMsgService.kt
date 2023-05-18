package com.scatl.uestcbbs.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.scatl.uestcbbs.IHeartMsgInterface
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.entity.HeartMsgBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.subscribeEx
import com.scatl.widget.floatview.FloatViewManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_NAME = "消息接收后台服务"
        const val CHANNEL_ID = 2000
        const val SERVICE_NAME = "com.scatl.uestcbbs.services.HeartMsgService"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(CHANNEL_ID.toString(), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                createNotificationChannel(mChannel)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification("若不想显示此通知，请至设置中关闭")
            startForeground(CHANNEL_ID, notification)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID.toString())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle("消息接收服务运行中")
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
//                    .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification_icon1)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mCompositeDisposable = CompositeDisposable()
        thread {
            while (iAmGroot) {
                try {
                    getMessageCount()
                    Thread.sleep(30000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        startService(Intent(this, HeartMsgGuardService::class.java))
        bindService(Intent(this, HeartMsgGuardService::class.java), serviceConnection, BIND_IMPORTANT)

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

//        val observable3 = RetrofitUtil
//            .getInstance()
//            .apiService
//            .homeInfo
//            .subscribeOn(Schedulers.io())

        val function = BiFunction<HeartMsgBean, String, HeartMsgBean> { p, s ->
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

//                val elementsHomeInfo = Jsoup.parse(t).select("div[class=bm bmw  flg cl]")
//                for (i in elementsHomeInfo.indices) {
//                    if (elementsHomeInfo[i].text().contains("我订阅的专辑")) {
//                        elementsHomeInfo[i].select("div[class=bm_c]").select("td[class=fl_g]").forEach {
//                            if (it.html().contains("forum_new")) {
//                                val id = BBSLinkUtil.getLinkInfo(it.select("dl").select("dt").select("a").attr("href")).id
//                                val collectionName = it.select("dl").select("dt").select("a").text()
//                                if (p.body.collectionBeans == null) {
//                                    p.body.collectionBeans = mutableListOf()
//                                }
//                                p.body.collectionBeans.add(HeartMsgBean.BodyBean.CollectionBean().apply {
//                                    cid = id
//                                    name = collectionName
//                                })
//                            }
//                        }
//                        break
//                    }
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            p
        }

        Observable
            .zip(observable1, observable2, function)
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
//                    if (MessageManager.INSTANCE.getUnreadMsgCount() > 0) {
//                        FloatViewManager.INSTANCE.get()?.show()
//                    } else {
//                        FloatViewManager.INSTANCE.get()?.hide()
//                    }

                    //notificationManager.notify(CHANNEL_ID, createNotification("三条新消息"))
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
        mCompositeDisposable?.clear()
    }

    override fun onBind(intent: Intent?) = Binder()

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            startService(Intent(this@HeartMsgService, HeartMsgGuardService::class.java))
            bindService(Intent(this@HeartMsgService, HeartMsgGuardService::class.java), this, BIND_IMPORTANT)
        }

    }

    class Binder: IHeartMsgInterface.Stub() {

        override fun getServiceName(): String {
            return HeartMsgService::class.java.name
        }

    }
}