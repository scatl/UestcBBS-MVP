package com.scatl.uestcbbs.module.post.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.PostDianPingBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.DianPingView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.ForumUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/4/13 9:33
 */
class DianPingPresenter: BaseVBPresenter<DianPingView>() {

    private val postModel = PostModel()

    fun getDianPingList(tid: Int, pid: Int, page: Int) {
        postModel.getCommentList(tid, pid, page, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                val html = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                    .replace("<root><![CDATA[", "").replace("]]></root>", "")
                try {
                    val document = Jsoup.parse(html)
                    val elements = document.select("div[class=pstl]")

                    val postDianPingBean = PostDianPingBean().apply {
                        list = mutableListOf()
                    }
                    for (i in elements.indices) {
                        val bean = PostDianPingBean.List()
                        bean.userName = elements[i].select("div[class=psti]").select("a[class=xi2 xw1]").text()
                        bean.comment = elements[i].getElementsByClass("psti")[0].text()
                            .replace(elements[i].select("div[class=psti]").select("span[class=xg1]").text(), "")
                            .replace(bean.userName + " ", "")
                        bean.date = elements[i].select("div[class=psti]").select("span[class=xg1]").text()
                            .replace("发表于 ", "")
                        bean.uid = BBSLinkUtil.getLinkInfo(elements[i].select("div[class=psti]").select("a[class=xi2 xw1]").attr("href")).id
                        bean.userAvatar = Constant.USER_AVATAR_URL + bean.uid
                        postDianPingBean.list.add(bean)
                    }
                    postDianPingBean.hasNext = s.contains("下一页")
                    mView?.onGetPostDianPingListSuccess(postDianPingBean)
                } catch (e: Exception) {
                    mView?.onGetPostDianPingListError("获取点评失败：" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetPostDianPingListError("获取点评失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}