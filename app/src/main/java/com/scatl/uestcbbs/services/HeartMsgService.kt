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
import com.scatl.uestcbbs.util.JsoupParseUtil
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.subscribeEx
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
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
            val notification = createNotification("若不想显示此通知，请至设置中关闭。")
            startForeground(CHANNEL_ID, notification)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID.toString())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle("消息接收服务运行中")
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
//            .setContentIntent(pendingIntent)
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
        val heartObservable = RetrofitUtil
            .getInstance()
            .apiService
            .getHeartMsg(Constant.SDK_VERSION)
            .subscribeOn(Schedulers.io())

        val collectionObservable = RetrofitUtil
            .getInstance()
            .apiService
            .getCollectionList(1, "my", "")
            .subscribeOn(Schedulers.io())

        val function1 = Function<Array<in HeartMsgBean>, HeartMsgBean> {
            it[0] as HeartMsgBean
        }

        val function2 = BiFunction<HeartMsgBean, String, HeartMsgBean> { p, collectionHtml ->
            val myCollections = JsoupParseUtil.parseCollectionList(collectionHtml)
            val collectionBean = HeartMsgBean.BodyBean.CollectionBean()
            myCollections.forEach {
                if (it.hasUnreadPost) {
                    collectionBean.count += 1
                }
            }
            p.body.collectionBean = collectionBean

            p
        }

        val observable = if (!SharePrefUtil.isOpenCollectionUpdateNotification(this)) {
            Observable.zip(arrayListOf(heartObservable), function1)
        } else {
            Observable.zip(heartObservable, collectionObservable, function2)
        }

        observable
            .subscribeEx(Observer<HeartMsgBean>().observer {
                onSuccess {
                    MessageManager.INSTANCE.pmUnreadCount = it.body?.pmInfos?.size?:0
                    MessageManager.INSTANCE.atUnreadCount = it.body?.atMeInfo?.count?:0
                    MessageManager.INSTANCE.replyUnreadCount = it.body?.replyInfo?.count?:0
                    MessageManager.INSTANCE.systemUnreadCount = it.body?.systemInfo?.count?:0
                    MessageManager.INSTANCE.dianPingUnreadCount = it.body?.pCommentInfoBean?.count?:0
                    MessageManager.INSTANCE.collectionUnreadCount = it.body?.collectionBean?.count?:0
                    //通知通知页面更新未读条数
                    EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
                    //notificationManager.notify(CHANNEL_ID, createNotification("三条新消息"))
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