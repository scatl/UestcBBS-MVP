package com.scatl.uestcbbs.module.credit.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.MineCreditBean.CreditHistoryBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.CreditHistoryView
import com.scatl.uestcbbs.util.subscribeEx
import org.jsoup.Jsoup

/**
 * Created by sca_tl on 2023/1/3 11:41
 */
class CreditHistoryPresenter: BaseVBPresenter<CreditHistoryView>() {

    private var creditModel = CreditModel()

    fun getCreditHistory(page: Int, creditType: Int, inOrOut: Int) {
        creditModel
            .getCreditHistory(page, creditType, inOrOut)
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("先登录才能")) {
                        mView?.onGetMineCreditHistoryError("请先获取Cookies后再进行本操作")
                    } else {
                        try {
                            val document = Jsoup.parse(it)
                            val elements = document.select("table[summary=主题付费]").select("tbody").select("tr")
                            val creditHistoryBeans: MutableList<CreditHistoryBean> = ArrayList()
                            for (i in 1 until elements.size) {
                                val historyBean = CreditHistoryBean()
                                historyBean.action = elements[i].select("td")[0].text()
                                historyBean.change = elements[i].select("td")[1].text()
                                historyBean.detail = elements[i].select("td")[2].text()
                                historyBean.time = elements[i].select("td")[3].text()
                                historyBean.link = elements[i].select("td")[2].select("a").attr("href")
                                historyBean.increase = historyBean.change.contains("+")
                                creditHistoryBeans.add(historyBean)
                            }
                            if (creditHistoryBeans.size == 0) {
                                mView?.onGetMineCreditHistoryError("啊哦，这里空空的")
                            } else {
                                mView?.onGetMineCreditHistorySuccess(creditHistoryBeans, it.contains("下一页"))
                            }
                        } catch (e: Exception) {
                            mView?.onGetMineCreditHistoryError("获取历史记录失败")
                            e.printStackTrace()
                        }
                    }
                }

                onError {
                    mView?.onGetMineCreditHistoryError("获取历史记录失败:${it.message}")
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }
}