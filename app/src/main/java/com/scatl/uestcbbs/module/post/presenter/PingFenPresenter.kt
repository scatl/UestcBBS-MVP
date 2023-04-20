package com.scatl.uestcbbs.module.post.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.RateUserBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.PingFenView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.ForumUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/4/20 16:29
 */
class PingFenPresenter: BaseVBPresenter<PingFenView>() {

    private val postModel = PostModel()

    fun getRateUser(tid: Int, pid: Int) {
        postModel.getRateUser(tid, pid, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val elements = document
                        .select("div[class=c floatwrap]")
                        .select("table[class=list]")
                        .select("tbody").select("tr")
                    val rateUserBeans: MutableList<RateUserBean> = ArrayList()
                    for (i in elements.indices) {
                        val rateUserBean = RateUserBean()
                        rateUserBean.credit = elements[i].select("td")[0].text()
                        rateUserBean.userName = elements[i].select("td")[1].select("a").text()
                        rateUserBean.uid = BBSLinkUtil.getLinkInfo(elements[i].select("td")[1].select("a").attr("href")).id
                        rateUserBean.time = elements[i].select("td")[2].text()
                        rateUserBean.reason = elements[i].select("td")[3].text()
                        rateUserBeans.add(rateUserBean)
                    }
                    mView?.onGetRateUserSuccess(rateUserBeans)
                } catch (e: Exception) {
                    mView?.onGetRateUserError("获取评分用户失败" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetRateUserError("获取评分用户失败" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}