package com.scatl.uestcbbs.services

import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.scatl.uestcbbs.util.showToast

class DayQuestionService : Service(), DayQuestionView {

    companion object {
        const val CHANNEL_NAME = "自动答题服务通知"
        const val NOTIFICATION_ID = 123456
        //const val MSG_START = "开始后台自动答题"
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
//        showToast(MSG_START, ToastType.TYPE_NORMAL)
    }

    override fun onGetDayQuestionError(msg: String?) {
        sendNotification(msg, questionNumber, true)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onDayQuestionFinished(msg: String?) {
        stopSelf()
    }

    override fun onConfirmFinishSuccess(msg: String?) {
        notificationManager.cancel(NOTIFICATION_ID)
        //sendNotification("答题完成，奖励已发放，明天再来哦", 7, title = "答题成功，水滴已发放🍻")
        showToast("答题成功，水滴已发放\uD83C\uDF7B", ToastType.TYPE_SUCCESS)
        stopSelf()
    }

    override fun onConfirmFinishError(msg: String?) {
        sendNotification("领取奖励失败，请稍后再试", 7)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onGetConfirmDspSuccess(dsp: String?, formHash: String?) {
        this.formHash = formHash
        mPresenter?.confirmNextQuestion(formHash)
        sendNotification("确认获取下一题中...", questionNumber, true)
//        showToast(MSG_START, ToastType.TYPE_NORMAL)
    }

    override fun onGetConfirmDspError(msg: String?) {
        sendNotification(msg, questionNumber)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onConfirmNextSuccess() {
        mPresenter?.getDayQuestion()
        sendNotification("正在获取下一题...", questionNumber, true)
    }

    override fun onConfirmNextError(msg: String?) {
        sendNotification("获取下一题失败", questionNumber)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onAnswerCorrect(question: String?, answer: String?) {
        mPresenter?.submitQuestionAnswer(question, answer)
        mPresenter?.getDayQuestion()
        sendNotification("答题正确，准备下一题", questionNumber, true)
    }

    override fun onAnswerIncorrect(msg: String?) {
        sendNotification(msg, questionNumber)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onAnswerError(msg: String?) {
        sendNotification(msg, questionNumber)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onFinishedAllCorrect(msg: String?, formHash: String?) {
        this.formHash = formHash
        mPresenter?.confirmFinishQuestion(this.formHash)
        sendNotification("恭喜，全部回答正确，正在领取奖励", 7, true)
//        showToast(MSG_START, ToastType.TYPE_NORMAL)
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
            sendNotification("未能提交答案", questionNumber)
        }
    }

    override fun onGetQuestionAnswerError(msg: String?) {
        sendNotification(msg, questionNumber, true)
        showToast(MSG_ERROR, ToastType.TYPE_ERROR)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendNotification(content: String?, progress: Int, indeterminate: Boolean = false, title: String = "") {
        val title1 = title.ifBlank { "后台答题中(${progress}/7)，请稍候..." }
        val notification = NotificationCompat
                .Builder(this, NOTIFICATION_ID.toString())
                .setGroupSummary(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_icon1)
                .setAutoCancel(false)
                .setContentTitle(title1)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setProgress(7, progress, indeterminate)
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}