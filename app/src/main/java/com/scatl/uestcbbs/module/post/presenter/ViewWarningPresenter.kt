package com.scatl.uestcbbs.module.post.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.ViewWarningItem
import com.scatl.uestcbbs.entity.ViewWarningEntity
import com.scatl.uestcbbs.helper.ExceptionHelper
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.ViewWarningView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * created by sca_tl at 2023/5/2 15:20
 */
class ViewWarningPresenter: BaseVBPresenter<ViewWarningView>() {

    val postModel = PostModel()

    fun viewWarning(tid: Int, uid: Int) {
        postModel.getWarningData(tid, uid, object : Observer<String>() {
            override fun OnSuccess(t: String?) {
                if (t?.contains("立即注册") == true && t.contains("用户登录")) {
                    mView?.onGetWarningDataError("cookies无效，请重新登录")
                } else {
                    try {
                        val document = Jsoup.parse(t)

                        val viewWarningEntity = ViewWarningEntity()
                        val dsp = document.select("div[class=bm bw0]").select("div[class=o pns]").text()
                        viewWarningEntity.dsp = dsp
                        document.select("div[class=bm bw0]")
                            .select("div[class=f_c]").select("table[class=list]")
                            .select("tbody").select("tr").forEach {
                                val item = ViewWarningItem()
                                item.name = it.select("td")[0].text()
                                item.uid = BBSLinkUtil.getLinkInfo(it.select("td")[0].select("a").attr("href")).id
                                item.avatar = Constant.USER_AVATAR_URL + item.uid
                                item.time = it.select("td")[1].text()
                                item.reason = it.select("td")[2].text()

                                viewWarningEntity.items?.add(item)
                        }
                        mView?.onGetWarningDataSuccess(viewWarningEntity)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mView?.onGetWarningDataError("获取数据失败${e.message}")
                    }
                }
            }

            override fun onError(e: ExceptionHelper.ResponseThrowable) {
                mView?.onGetWarningDataError("获取数据失败${e.message}")
            }

            override fun OnCompleted() {
            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}