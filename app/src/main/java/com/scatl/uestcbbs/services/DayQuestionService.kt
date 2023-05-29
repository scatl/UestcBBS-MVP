package com.scatl.uestcbbs.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.entity.DayQuestionBean
import com.scatl.uestcbbs.module.dayquestion.presenter.DayQuestionPresenter
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionView
import com.scatl.uestcbbs.receivers.RetryDayQuestionReceiver
import com.scatl.uestcbbs.util.showToast

class DayQuestionService : Service(), DayQuestionView {

    companion object {
        const val CHANNEL_NAME = "自动答题服务通知"
        const val NOTIFICATION_ID = 123456
        const val MSG_START = "开始后台自动答题"
        const val MSG_ERROR = "自动答题失败了，下拉查看详情"
    }

    private var mPresenter: DayQuestionPresenter? = null
    private var formHash: String? = null
    private lateinit var mDayQuestionBean: DayQuestionBean
    private var questionNumber = 1
    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(NotificationChannel(
                    NOTIFICATION_ID.toString(),
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mPresenter = DayQuestionPresenter()
        mPresenter?.attachView(this)
        mPresenter?.getDayQuestion()
        return START_STICKY
    }

    override fun onGetDayQuestionSuccess(dayQuestionBean: DayQuestionBean) {
        formHash = dayQuestionBean.formHash
        mDayQuestionBean = dayQuestionBean
        mPresenter?.getQuestionAnswer(dayQuestionBean.questionTitle)
        questionNumber = dayQuestionBean.questionNum
        sendNotification("获取题目成功，正在获取答案", questionNumber)
        //showToast(MSG_START, ToastType.TYPE_NORMAL)
    }

    override fun onGetDayQuestionError(msg: String?, netError: Boolean) {
        if (!netError) {
            sendNotification(msg, questionNumber, true, error = true)
        } else {
            stopSelf()
        }
    }

    override fun onDayQuestionFinished(msg: String?) {
        stopSelf()
    }

    override fun onConfirmFinishSuccess(msg: String?) {
        notificationManager.cancel(NOTIFICATION_ID)
        showToast("答题成功，水滴已发放\uD83C\uDF7B", ToastType.TYPE_SUCCESS)
        stopSelf()
    }

    override fun onConfirmFinishError(msg: String?) {
        sendNotification("领取奖励失败，请稍后再试", 7, error = true)
    }

    override fun onGetConfirmDspSuccess(dsp: String?, formHash: String?) {
        this.formHash = formHash
        mPresenter?.confirmNextQuestion(formHash)
        sendNotification("确认获取下一题中...", questionNumber, true)
    }

    override fun onGetConfirmDspError(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onConfirmNextSuccess() {
        mPresenter?.getDayQuestion()
        sendNotification("正在获取下一题...", questionNumber, true)
    }

    override fun onConfirmNextError(msg: String?) {
        sendNotification("获取下一题失败", questionNumber, error = true)
    }

    override fun onAnswerCorrect(question: String?, answer: String?) {
        mPresenter?.submitQuestionAnswer(question, answer)
        mPresenter?.getDayQuestion()
        sendNotification("答题正确，准备下一题", questionNumber, true)
    }

    override fun onAnswerIncorrect(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onAnswerError(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onFinishedAllCorrect(msg: String?, formHash: String?) {
        this.formHash = formHash
        mPresenter?.confirmFinishQuestion(this.formHash)
        sendNotification("恭喜，全部回答正确，正在领取奖励", 7, true)
    }

    override fun onGetQuestionAnswerSuccess(answer: String) {
        var answerIndex = -1
        for ((index, value) in mDayQuestionBean.options.withIndex()) {
            if (answer == value.dsp) {
                answerIndex = index
                break
            }
        }
        if (answerIndex in mDayQuestionBean.options.indices) {
            mPresenter?.submitQuestion(
                formHash,
                mDayQuestionBean.options[answerIndex].answerValue,
                mDayQuestionBean.questionTitle,
                mDayQuestionBean.options[answerIndex].dsp
            )
        } else {
            sendNotification("未能提交答案", questionNumber, error = true)
        }
    }

    override fun onGetQuestionAnswerError(msg: String?) {
        sendNotification(msg, questionNumber, true, error = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendNotification(content: String?,
                                 progress: Int,
                                 indeterminate: Boolean = false,
                                 title: String = "",
                                 error: Boolean = false) {
        val title1 = title.ifBlank { "后台答题中(${progress}/7)，请稍候..." }
        val builder = NotificationCompat
                .Builder(this, NOTIFICATION_ID.toString())
                .setGroupSummary(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setAutoCancel(false)
                .setContentTitle(title1)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setProgress(7, progress, indeterminate)
        if (error) {
            val intent = Intent(this, RetryDayQuestionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val action = NotificationCompat.Action.Builder(0, "重试", pendingIntent).build()
            builder.addAction(action)
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        if (error) {
            showToast(MSG_ERROR, ToastType.TYPE_ERROR)
            stopSelf()
        }
    }
}