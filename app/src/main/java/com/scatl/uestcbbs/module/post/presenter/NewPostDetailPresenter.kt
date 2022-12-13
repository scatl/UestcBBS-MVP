package com.scatl.uestcbbs.module.post.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BasePresenter
import com.scatl.uestcbbs.entity.*
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.NewPostDetailView
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.ForumUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by sca_tl on 2022/12/5 10:55
 */
class NewPostDetailPresenter: BasePresenter<NewPostDetailView>() {

    private val postModel = PostModel()

    fun getPostDetail(page: Int, pageSize: Int, order: Int, topicId: Int, authorId: Int) {
        postModel.getPostDetail(page, pageSize, order, topicId, authorId,
            SharePrefUtil.getToken(view.getContext()), SharePrefUtil.getSecret(view.getContext()),
            object : Observer<PostDetailBean>() {
                override fun OnSuccess(postDetailBean: PostDetailBean) {
                    if (postDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onGetPostDetailSuccess(postDetailBean)
                    } else {
                        view.onGetPostDetailError(postDetailBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onGetPostDetailError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

    fun vote(tid: Int, boardId: Int, options: List<Int>) {
        postModel.vote(tid, boardId,
            options.toString().replace("[", "").replace("]", ""),
            SharePrefUtil.getToken(view.getContext()), SharePrefUtil.getSecret(view.getContext()),
            object : Observer<VoteResultBean>() {
                override fun OnSuccess(voteResultBean: VoteResultBean) {
                    if (voteResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onVoteSuccess(voteResultBean)
                    } else if (voteResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        view.onVoteError(voteResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onVoteError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

    fun favorite(idType: String, action: String, id: Int) {
        postModel.favorite(idType, action, id, SharePrefUtil.getToken(view.getContext()), SharePrefUtil.getSecret(view.getContext()),
            object : Observer<FavoritePostResultBean>() {
                override fun OnSuccess(favoritePostResultBean: FavoritePostResultBean) {
                    if (favoritePostResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onFavoritePostSuccess(favoritePostResultBean)
                    }
                    if (favoritePostResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        view.onFavoritePostError(favoritePostResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onFavoritePostError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

    fun support(tid: Int, pid: Int, type: String, action: String) {
        postModel.support(tid, pid, type, action,
            SharePrefUtil.getToken(view.getContext()),
            SharePrefUtil.getSecret(view.getContext()),
            object : Observer<SupportResultBean>() {
                override fun OnSuccess(supportResultBean: SupportResultBean) {
                    if (supportResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onSupportSuccess(supportResultBean, action, type)
                    }
                    if (supportResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        view.onSupportError(supportResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    view.onSupportError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    disposable.add(d)
                }
            })
    }

    fun getPostWebDetail(tid: Int, page: Int) {
        postModel.getPostWebDetail(tid, page, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("立即注册") && s.contains("找回密码") && s.contains("自动登录")) {

                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val postWebBean = PostWebBean()
                        postWebBean.favoriteNum = document.select("span[id=favoritenumber]").text()
                        postWebBean.formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                        postWebBean.rewardInfo = document.select("td[class=plc ptm pbm xi1]").text()
                        postWebBean.shengYuReword = document.select("td[class=pls vm ptm]").text()
                        postWebBean.originalCreate = document.select("div[id=threadstamp]").html().contains("原创")
                        postWebBean.essence = document.select("div[id=threadstamp]").html().contains("精华")
                        postWebBean.topStick = document.select("div[id=threadstamp]").html().contains("置顶")
                        postWebBean.supportCount = document.select("em[id=recommendv_add_digg]").text().toInt()
                        postWebBean.againstCount = document.select("em[id=recommendv_sub_digg]").text().toInt()
                        postWebBean.actionHistory = document.select("div[class=modact]").select("a").text()
                        postWebBean.collectionList = ArrayList()
                        document.select("ul[class=mbw xl xl2 cl]").select("li")?.let {
                            for (i in it.indices) {
                                val collection = PostWebBean.Collection()
                                collection.name = it[i].select("a").text()
                                collection.subscribeCount = it[i].select("span[class=xg1]").text()
                                collection.ctid = ForumUtil.getFromLinkInfo(it[i].select("a").attr("href")).id
                                postWebBean.collectionList.add(collection)
                            }
                        }
                        view.onGetPostWebDetailSuccess(postWebBean)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(e: ResponseThrowable) { }

            override fun OnCompleted() { }

            override fun OnDisposable(d: Disposable) {
                disposable.add(d)
            }
        })
    }

    fun getDianPingList(tid: Int, pid: Int, page: Int) {
        postModel.getCommentList(tid, pid, page, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                val html = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                    .replace("<root><![CDATA[", "").replace("]]></root>", "")
                try {
                    val postDianPingBeans: MutableList<PostDianPingBean> = ArrayList()
                    val document = Jsoup.parse(html)
                    val elements = document.select("div[class=pstl]")
                    for (i in elements.indices) {
                        val postDianPingBean = PostDianPingBean()
                        postDianPingBean.userName = elements[i].select("div[class=psti]").select("a[class=xi2 xw1]").text()
                        postDianPingBean.comment =
                            elements[i].getElementsByClass("psti")[0].text()
                                .replace(elements[i].select("div[class=psti]").select("span[class=xg1]").text(), "")
                                .replace(postDianPingBean.userName + " ", "")
                        postDianPingBean.date = elements[i].select("div[class=psti]").select("span[class=xg1]").text()
                                .replace("发表于 ", "")
                        postDianPingBean.uid = ForumUtil.getFromLinkInfo(
                            elements[i].select("div[class=psti]").select("a[class=xi2 xw1]").attr("href")).id
                        postDianPingBean.userAvatar = Constant.USER_AVATAR_URL + postDianPingBean.uid
                        postDianPingBeans.add(postDianPingBean)
                    }
                    view.onGetPostDianPingListSuccess(postDianPingBeans, s.contains("下一页"))
                } catch (e: java.lang.Exception) {

                }
            }

            override fun onError(e: ResponseThrowable) {

            }

            override fun OnCompleted() { }

            override fun OnDisposable(d: Disposable) {
                disposable.add(d)
            }
        })
    }
}