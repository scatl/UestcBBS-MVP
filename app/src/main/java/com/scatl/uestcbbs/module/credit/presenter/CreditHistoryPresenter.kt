package com.scatl.uestcbbs.module.credit.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.MineCreditBean.CreditHistoryBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.credit.model.CreditModel
import com.scatl.uestcbbs.module.credit.view.CreditHistoryView
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl on 2023/1/3 11:41
 */
class CreditHistoryPresenter: BaseVBPresenter<CreditHistoryView>() {

    private var creditModel = CreditModel()

    fun getCreditHistory(page: Int, creditType: Int, inOrOut: Int) {
        creditModel.getCreditHistory(page, creditType, inOrOut, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("先登录才能")) {
                    mView?.onGetMineCreditHistoryError("请先获取Cookies后再进行本操作")
                } else {
                    try {
                        val document = Jsoup.parse(s)
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
                            mView?.onGetMineCreditHistorySuccess(creditHistoryBeans, s.contains("下一页"))
                        }
                    } catch (e: Exception) {
                        mView?.onGetMineCreditHistoryError("获取历史记录失败")
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetMineCreditHistoryError("获取历史记录失败")
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}