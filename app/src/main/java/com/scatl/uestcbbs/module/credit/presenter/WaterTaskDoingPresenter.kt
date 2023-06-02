package com.scatl.uestcbbs.module.credit.presenter

import android.widget.TextView
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.WaterTaskDoingView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/4/6 20:44
 */
class WaterTaskDoingPresenter: BaseVBPresenter<WaterTaskDoingView>() {

    private val creditModel = CreditModel()

    fun getDoingTaskList() {
        creditModel.getDoingTask(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("需要先登录")) {
                    mView?.onGetDoingTaskError("请获取Cookies后进行此操作")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                        val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                        SharePrefUtil.setForumHash(App.getContext(), formhash)
                        val taskBeans: MutableList<TaskBean> = ArrayList()
                        for (i in elements.indices) {
                            val taskBean = TaskBean()
                            taskBean.type = TaskType.TYPE_DOING
                            taskBean.name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            taskBean.id = BBSLinkUtil.getLinkInfo(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href")).id
                            taskBean.popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            taskBean.dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            taskBean.award = elements[i].select("td[class=xi1 bbda hm]").text()
                            taskBean.progress = elements[i].select("td[class=bbda ptm pbm]")
                                .select("div[class=xs0]").text().replace("已完成 ", "")
                                .replace("%", "").toDouble()
                            taskBean.icon = ApiConstant.BBS_BASE_URL + elements[i].select("td")[0].select("img").attr("src")
                            taskBeans.add(taskBean)
                        }
                        mView?.onGetDoingTaskSuccess(taskBeans, formhash)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mView?.onGetDoingTaskError("获取任务失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetDoingTaskError("获取任务失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun deleteDoingTask(id: Int, position: Int) {
        creditModel.deleteDoingTask(id, SharePrefUtil.getForumHash(App.getContext()), object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val msg = document.select("div[id=messagetext]").text()
                    mView?.onDeleteDoingTaskSuccess(msg, position)
                } catch (e: java.lang.Exception) {
                    mView?.onDeleteDoingTaskError("放弃任务失败：" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onDeleteDoingTaskError("放弃任务失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getTaskAward(position: Int, id: Int) {
        creditModel.getTaskAward(id, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val msg = document.select("div[id=messagetext]").text()

                    //恭喜您，任务已成功完成，您将收到奖励通知，请注意查收
                    //您已完成该任务的 35.00%，还有4小时10分钟时间，加油啊！ 如果您的浏览器没有自动跳转，请点击此链接
                    if (msg.contains("恭喜")) {
                        mView?.onGetTaskAwardSuccess(msg, position)
                    } else if (msg.contains("加油啊")) {
                        mView?.onGetTaskAwardError("领取奖励失败：$msg")
                    }

                } catch (e: Exception) {
                    mView?.onGetTaskAwardError("领取奖励失败：" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetTaskAwardError("领取奖励失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun checkLeftTime(id: Int, textView: TextView? = null) {
        creditModel.getTaskAward(id, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val msg = document.select("div[id=messagetext]").text()

                    //恭喜您，任务已成功完成，您将收到奖励通知，请注意查收
                    //您已完成该任务的 35.00%，还有4小时10分钟时间，加油啊！ 如果您的浏览器没有自动跳转，请点击此链接
                    if (msg.contains("时间")) {
                        val matcher = Pattern.compile(".*?还有(.*?)时间").matcher(msg)
                        if (matcher.find()) {
                            mView?.onCheckLeftTimeSuccess(matcher.group(1), textView)
                        }
                    }
                } catch (e: Exception) {
                    mView?.onCheckLeftTimeError("查看剩余时间失败：" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onCheckLeftTimeError("查看剩余时间失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}