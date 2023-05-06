package com.scatl.uestcbbs.module.collection.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.entity.CollectionDetailBean.PostListBean
import com.scatl.uestcbbs.entity.CollectionDetailBean.RecentSubscriberBean
import com.scatl.uestcbbs.entity.CollectionDetailBean.SameOwnerCollection
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.collection.model.CollectionModel
import com.scatl.uestcbbs.module.collection.view.CollectionDetailView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/5/4 14:20
 */
class CollectionDetailPresenter: BaseVBPresenter<CollectionDetailView>() {

    private val collectionModel = CollectionModel()

    fun getCollectionDetail(ctid: Int, page: Int) {
        collectionModel.getCollectionDetail(ctid, page, object : Observer<String>() {
            override fun OnSuccess(html: String) {
                try {
                    val collectionDetailBean = CollectionDetailBean().apply {
                        mRecentSubscriberBean = ArrayList()
                        authorOtherCollection = ArrayList()
                        postListBean = ArrayList()
                        mSameOwnerCollection = ArrayList()
                    }

                    val document = Jsoup.parse(html)
                    val formhash = document.select("div[class=hdc]").select("div[class=wp]")
                        .select("div[class=cl]").select("form[id=scbar_form]")
                        .select("input[name=formhash]").attr("value")

                    if (!formhash.isNullOrEmpty()) {
                        SharePrefUtil.setForumHash(mView?.getContext(), formhash)
                    }

                    val mainData = document.select("div[class=ct2 wp cl]").select("div[class=mn]").select("div[class=bm bml pbn]")

                    collectionDetailBean.collectionTitle = mainData.select("h1[class=xs2 z]").text()
                    collectionDetailBean.subscribeCount = mainData.select("div[class=clct_flw]").select("strong[id=follownum_display]").text()
                    collectionDetailBean.isSubscribe = mainData.select("div[class=clct_flw]").select("a").text() == "取消订阅"
                    collectionDetailBean.collectionDsp = mainData.select("div[class=bm_c]").select("div").last().text()

                    val authorData = mainData.select("div[class=bm_c]").select("div[class=mbn cl]").select("p").last()
                    collectionDetailBean.collectionAuthorLink = authorData.select("a").first()?.attr("href")
                    collectionDetailBean.collectionAuthorId = BBSLinkUtil.getLinkInfo(collectionDetailBean.collectionAuthorLink).id
                    collectionDetailBean.collectionAuthorAvatar = Constant.USER_AVATAR_URL.plus(collectionDetailBean.collectionAuthorId)
                    collectionDetailBean.collectionAuthorName = authorData.select("a").first()?.text()

                    collectionDetailBean.collectionTags = mainData.select("div[class=bm_c]").select("div[class=mbn cl]").select("p[class=mbn]").select("a").eachText()
                    collectionDetailBean.ratingScore = mainData.select("div[class=ptn pbn xg1 cl]").attr("title").toFloat()
                    collectionDetailBean.ratingTitle = mainData.select("div[class=ptn pbn xg1 cl]").text()

                    val topics = document.select("div[class=ct2 wp cl]").select("div[class=mn]").select("div[class=tl bm]").select("div[class=bm_c]").select("tr")
                    for (i in topics.indices) {
                        val postListBean = PostListBean()
                        postListBean.topicTitle = topics[i].select("th").select("a").attr("title")
                        postListBean.topicLink = topics[i].select("th").select("a").attr("href")
                        postListBean.topicId = BBSLinkUtil.getLinkInfo(postListBean.topicLink).id

                        val postListAuthorData = topics[i].select("td[class=by]")[0].select("cite")
                        postListBean.authorLink = postListAuthorData.select("a").attr("href")
                        postListBean.authorName = postListAuthorData.select("a").text()
                        postListBean.authorId = BBSLinkUtil.getLinkInfo(postListBean.authorLink).id
                        postListBean.authorAvatar = Constant.USER_AVATAR_URL.plus(postListBean.authorId)

                        postListBean.postDate = topics[i].select("td[class=by]")[0].select("em[class=xi1]").text()
                        postListBean.commentCount = topics[i].select("td[class=num]").select("a").text()
                        postListBean.viewCount = topics[i].select("td[class=num]").select("em").text()

                        val lastPostData = topics[i].select("td[class=by]")[1].select("cite")
                        postListBean.lastPostAuthorLink = lastPostData.select("a").attr("href")
                        postListBean.lastPostAuthorName = lastPostData.select("a").text()
                        postListBean.lastPostAuthorId = BBSLinkUtil.getLinkInfo(postListBean.lastPostAuthorLink).id
                        postListBean.lastPostAuthorAvatar = Constant.USER_AVATAR_URL.plus(postListBean.lastPostAuthorId)
                        postListBean.lastPostDate = topics[i].select("td[class=by]")[1].select("em").text()

                        collectionDetailBean.postListBean.add(postListBean)
                    }

                    val a = document.select("div[class=ct2 wp cl]").select("div[class=sd]").select("div[class=bm]")
                    if (a.size > 0) {
                        val recentSubscriberData = a[0].select("div[class=bm_c]").select("ul[class=ml mls cl]").select("li")
                        for (item in recentSubscriberData) {
                            val r = RecentSubscriberBean().apply {
                                userName = item.select("p").text()
                                userId = BBSLinkUtil.getLinkInfo(item.select("p").select("a").attr("href")).id
                                userAvatar = Constant.USER_AVATAR_URL.plus(userId)
                            }
                            collectionDetailBean.mRecentSubscriberBean.add(r)
                        }
                    }
                    if (a.size > 1) {
                        val sameOwnerCollectionData = a[1].select("div[class=bm_c]").select("div[class=pbn]")
                        for (item in sameOwnerCollectionData) {
                            val r = SameOwnerCollection().apply {
                                name = item.select("a").text()
                                cid = BBSLinkUtil.getLinkInfo(item.select("a").attr("href")).id
                            }
                            collectionDetailBean.mSameOwnerCollection.add(r)
                        }
                    }

                    mView?.onGetCollectionSuccess(collectionDetailBean, html.contains("下一页"))
                } catch (e: Exception) {
                    mView?.onGetCollectionError("数据解析失败:" + e.message)
                    e.printStackTrace()
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetCollectionError("获取数据失败" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun deleteCollectionPost(citd: Int, tid: Int, position: Int) {
        collectionModel.deleteCollectionPost(SharePrefUtil.getForumHash(mView?.getContext()),
            tid, citd, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val info = document.select("div[id=messagetext]").text()
                    if (info.contains("删除淘专辑内主题成功")) {
                        mView?.onDeleteCollectionPostSuccess(info, position)
                    } else {
                        mView?.onDeleteCollectionPostError(info)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    mView?.onDeleteCollectionPostError("删除失败:" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onDeleteCollectionPostError("删除失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun deleteCollection(ctid: Int) {
        collectionModel.deleteCollection(SharePrefUtil.getForumHash(mView?.getContext()), ctid, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val info = document.select("div[id=messagetext]").text()
                    if (info.contains("删除淘专辑成功")) {
                        mView?.onDeleteCollectionSuccess(info)
                    } else {
                        mView?.onDeleteCollectionError(info)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    mView?.onDeleteCollectionError("删除失败:" + e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onDeleteCollectionError("删除失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun subscribeCollection(ctid: Int, op: String) {
        collectionModel.subscribeCollection(ctid, op,
            SharePrefUtil.getForumHash(mView?.getContext()), object : Observer<String>() {
            override fun OnSuccess(html: String) {
                if (html.contains("未定义")) {
                    mView?.onSubscribeCollectionError("操作失败，请重新登录")
                } else if (html.contains("成功订阅")) {
                    mView?.onSubscribeCollectionSuccess(true)
                } else if (html.contains("取消订阅")) {
                    mView?.onSubscribeCollectionSuccess(false)
                } else {
                    mView?.onSubscribeCollectionError("未知错误，请联系开发者")
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onSubscribeCollectionError("操作失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}