package com.scatl.uestcbbs.module.credit.presenter

import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.WaterTaskNewView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.subscribeEx
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/4/7 18:01
 */
open class WaterTaskNewPresenter<T: WaterTaskNewView>: BaseVBPresenter<T>() {

    private val creditModel = CreditModel()

    fun getNewTaskList() {
        creditModel
            .getNewTask()
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("需要先登录")) {
                        mView?.onGetNewTaskError("请获取Cookies后进行此操作")
                    } else {
                        try {
                            val document = Jsoup.parse(it)
                            val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                            val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                            SharePrefUtil.setForumHash(App.getContext(), formhash)
                            val taskBeans: MutableList<TaskBean> = ArrayList()
                            for (i in elements.indices) {
                                val taskBean = TaskBean().apply {
                                    type = TaskType.TYPE_NEW
                                    name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                                    id = BBSLinkUtil.getLinkInfo(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href")).id
                                    popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                                    dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                                    award = elements[i].select("td[class=xi1 bbda hm]").text()
                                    icon = ApiConstant.BBS_BASE_URL + elements[i].select("td")[0].select("img").attr("src")

                                }
                                taskBeans.add(taskBean)
                            }
                            mView?.onGetNewTaskSuccess(taskBeans, formhash)
                        } catch (e: Exception) {
                            mView?.onGetNewTaskError("获取任务失败：${e.message}")
                        }
                    }
                }

                onError {
                    mView?.onGetNewTaskError("获取任务失败：${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun applyNewTask(id: Int, position: Int) {
        creditModel
            .applyNewTask(id)
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    try {
                        val document = Jsoup.parse(it)
                        val msg = document.select("div[id=messagetext]").text()
                        mView?.onApplyNewTaskSuccess(msg, id, position)
                    } catch (e: Exception) {
                        mView?.onApplyNewTaskError("申请任务失败：${e.message}")
                    }
                }

                onError {
                    mView?.onApplyNewTaskError("申请任务失败：${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}