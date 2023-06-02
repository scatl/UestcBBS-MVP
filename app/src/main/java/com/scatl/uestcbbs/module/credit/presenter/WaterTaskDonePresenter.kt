package com.scatl.uestcbbs.module.credit.presenter

import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.helper.ExceptionHelper
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.WaterTaskDoneView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * created by sca_tl at 2023/4/7 19:42
 */
class WaterTaskDonePresenter: BaseVBPresenter<WaterTaskDoneView>() {

    private val creditModel = CreditModel()

    fun getDoneTaskList() {
        creditModel.getDoneTask(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("需要先登录")) {
                    mView?.onGetDoneTaskError("请获取Cookies后进行此操作")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                        val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                        SharePrefUtil.setForumHash(App.getContext(), formhash)
                        val taskBeans: MutableList<TaskBean> = ArrayList()
                        for (i in elements.indices) {
                            val taskBean = TaskBean()
                            taskBean.type = TaskType.TYPE_DONE
                            taskBean.name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            taskBean.id = BBSLinkUtil.getLinkInfo(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href")).id
                            taskBean.popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            taskBean.dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            taskBean.award = elements[i].select("td[class=xi1 bbda hm]").text()
                            taskBean.icon = ApiConstant.BBS_BASE_URL + elements[i].select("td")[0].select("img").attr("src")
                            taskBean.doneTime = elements[i].select("td[class=bbda]").getOrNull(1)?.text()
                            taskBeans.add(taskBean)
                        }
                        mView?.onGetDoneTaskSuccess(taskBeans, formhash)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mView?.onGetDoneTaskError("获取任务失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ExceptionHelper.ResponseThrowable) {
                mView?.onGetDoneTaskError("获取任务失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}