package com.scatl.uestcbbs.module.collection.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CollectionListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.collection.model.CollectionModel
import com.scatl.uestcbbs.module.collection.view.CollectionListView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by tanlei02 at 2023/5/5 11:43
 */
class CollectionListPresenter: BaseVBPresenter<CollectionListView>() {

    private val collectionModel = CollectionModel()

    fun getCollectionList(page: Int, op: String, order: String) {
        collectionModel.getCollectionList(page, op, order, object : Observer<String>() {
            override fun OnSuccess(html: String) {
                try {
                    val collectionBeans: MutableList<CollectionListBean> = ArrayList()

                    val document = Jsoup.parse(html)
                    val elements = document.select("div[class=clct_list cl]").select("div[class=xld xlda cl]")

                    for (i in elements.indices) {
                        val collectionBean = CollectionListBean().apply {
                            collectionTags = ArrayList()
                        }

                        val dd1Data = elements[i].select("dd[class=m hm]")
                        collectionBean.collectionLink = dd1Data.select("a").attr("href")
                        collectionBean.collectionId = BBSLinkUtil.getLinkInfo(collectionBean.collectionLink).id

                        val dtData = elements[i].select("dt[class=xw1]")
                        if (dtData.select("a").size > 0) {
                            collectionBean.collectionTitle = dtData.select("a")[0].text()
                        }
                        collectionBean.collectionTags = dtData.select("span[class=ctag_keyword]").select("a").eachText()
                        collectionBean.createByMe = dtData.text().contains("我创建的")
                        collectionBean.subscribeByMe = dtData.text().contains("我订阅的")

                        if (elements[i].select("dd").size > 1) {
                            val dd2Data = elements[i].select("dd")[1]

                            collectionBean.authorLink = dd2Data.select("p[class=xg1]").select("a").attr("href")
                            collectionBean.authorId = BBSLinkUtil.getLinkInfo(collectionBean.authorLink).id
                            collectionBean.authorName = dd2Data.select("p[class=xg1]").select("a").text()
                            collectionBean.authorAvatar = Constant.USER_AVATAR_URL.plus(collectionBean.authorId)
                            collectionBean.latestUpdateDate = dd2Data.select("p[class=xg1]")[0].ownText().replace("创建, 最后更新 ", "")

                            if (dd2Data.select("p").size > 0) {
                                collectionBean.collectionDsp = dd2Data.select("p")[0].text()
                            }

                            when(dd1Data.select("span").text()) {
                                "主题" -> {
                                    collectionBean.postCount = dd1Data.select("a").select("strong[class=xi2]").text()
                                    val matcher = Pattern.compile("订阅 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                                    if (matcher.find()) {
                                        collectionBean.subscribeCount = matcher.group(1)
                                        collectionBean.commentCount = matcher.group(2)
                                    }
                                }
                                "评论" -> {
                                    collectionBean.commentCount = dd1Data.select("a").select("strong[class=xi2]").text()
                                    val matcher = Pattern.compile("订阅 (\\d+), 主题 (\\d+)").matcher(dd2Data.select("p")[1].text())
                                    if (matcher.find()) {
                                        collectionBean.subscribeCount = matcher.group(1)
                                        collectionBean.postCount = matcher.group(2)
                                    }
                                }
                                "订阅" -> {
                                    collectionBean.subscribeCount = dd1Data.select("a").select("strong[class=xi2]").text()
                                    val matcher = Pattern.compile("主题 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                                    if (matcher.find()) {
                                        collectionBean.postCount = matcher.group(1)
                                        collectionBean.commentCount = matcher.group(2)
                                    }
                                }
                            }
                            if (dd2Data.select("p").size > 3) {
                                collectionBean.latestPostTitle = dd2Data.select("p")[3].select("a").text()
                                collectionBean.latestPostLink = dd2Data.select("p")[3].select("a").attr("href")
                                collectionBean.latestPostId = BBSLinkUtil.getLinkInfo(collectionBean.latestPostLink).id
                            }
                        }

                        collectionBeans.add(collectionBean)
                    }
                    mView?.onGetCollectionListSuccess(collectionBeans, html.contains("下一页"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    mView?.onGetCollectionListError("数据解析失败:" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetCollectionListError("获取数据失败" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}