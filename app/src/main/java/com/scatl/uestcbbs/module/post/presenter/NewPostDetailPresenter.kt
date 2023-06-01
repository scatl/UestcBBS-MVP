package com.scatl.uestcbbs.module.post.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.FavoritePostResultBean
import com.scatl.uestcbbs.entity.HistoryBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.PostDianPingBean
import com.scatl.uestcbbs.entity.PostWebBean
import com.scatl.uestcbbs.entity.SupportResultBean
import com.scatl.uestcbbs.entity.VoteResultBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.NewPostDetailView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.subscribeEx
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by sca_tl on 2022/12/5 10:55
 */
class NewPostDetailPresenter: BaseVBPresenter<NewPostDetailView>() {

    private val postModel = PostModel()

    fun getDetail(page: Int, pageSize: Int, order: Int, topicId: Int, authorId: Int) {
        val observable1 = RetrofitUtil
            .getInstance()
            .apiService
            .getPostDetailList(page, pageSize, order, topicId, authorId,
                SharePrefUtil.getToken(mView?.getContext()), SharePrefUtil.getSecret(mView?.getContext()))
            .subscribeOn(Schedulers.io())

        val observable2 = RetrofitUtil
            .getInstance()
            .apiService
            .getPostWebDetail(topicId, 1)
            .subscribeOn(Schedulers.io())

        val function = BiFunction<PostDetailBean, String, PostDetailBean> { p, s ->
            if (!s.contains("尚未登陆") || !s.contains("对不起，该版块仅限电子科技大学校园网内访问")) {
                try {
                    val pid = p.topic?.reply_posts_id

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
                            collection.ctid = BBSLinkUtil.getLinkInfo(it[i].select("a").attr("href")).id
                            postWebBean.collectionList.add(collection)
                        }
                    }

                    val modifyMatcher = Pattern.compile("本帖最后由(.*?)于(.*?)编辑").matcher(s)
                    if (modifyMatcher.find()) {
                        postWebBean.modifyHistory = modifyMatcher.group()
                    }

                    val post = document.select("div[id=postlist]").select("div")[0].select("td[class=plc]").select("div[class=pi]")
                    if (post.isNotEmpty()) {
                        postWebBean.isWarned = post[0].html().contains("action=viewwarning")
                    }

                    val dianPingBean = PostDianPingBean()
                    dianPingBean.list = mutableListOf()
                    document.select("div[id=postlist]").select("div[id=post_$pid]").select("div[id=comment_$pid]")
                        .select("div[class=pstl xs1 cl]")?.forEach {
                            val bean = PostDianPingBean.List()
                            bean.uid = BBSLinkUtil.getLinkInfo(it.select("div[class=psta vm]").select("a[class=xi2 xw1]").attr("href")).id
                            bean.userName = it.select("div[class=psta vm]").select("a[class=xi2 xw1]").text()
                            bean.userAvatar = Constant.USER_AVATAR_URL.plus(bean.uid)
                            bean.comment = it.select("div[class=psti]").getOrNull(0)?.ownText()
                            bean.date = it.select("div[class=psti]").select("span").text()?.replace("发表于 ", "")
                            dianPingBean.list.add(bean)
                        }
                    dianPingBean.hasNext = document.select("div[id=postlist]").select("div[post_$pid]")
                        .select("div[id=comment_$pid]").select("div[class=pgs mbm cl]")?.isEmpty() == true
                    postWebBean.dianPingBean = dianPingBean

                    p.postWebBean = postWebBean
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            p
        }

        Observable
            .zip(observable1, observable2, function)
            .subscribeEx(com.scatl.uestcbbs.http.Observer<PostDetailBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPostDetailSuccess(it)
                    } else {
                        mView?.onGetPostDetailError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetPostDetailError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })

    }

    fun vote(tid: Int, boardId: Int, options: List<Int>) {
        postModel.vote(tid, boardId,
            options.toString().replace("[", "").replace("]", ""),
            object : Observer<VoteResultBean>() {
                override fun OnSuccess(voteResultBean: VoteResultBean) {
                    if (voteResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onVoteSuccess(voteResultBean)
                    } else if (voteResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onVoteError(voteResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onVoteError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun favorite(idType: String, action: String, id: Int) {
        postModel.favorite(idType, action, id,
            object : Observer<FavoritePostResultBean>() {
                override fun OnSuccess(favoritePostResultBean: FavoritePostResultBean) {
                    if (favoritePostResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onFavoritePostSuccess(favoritePostResultBean)
                    }
                    if (favoritePostResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onFavoritePostError(favoritePostResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onFavoritePostError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun support(tid: Int, pid: Int, type: String, action: String) {
        postModel.support(tid, pid, type, action,
            object : Observer<SupportResultBean>() {
                override fun OnSuccess(supportResultBean: SupportResultBean) {
                    if (supportResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSupportSuccess(supportResultBean, action, type)
                    }
                    if (supportResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSupportError(supportResultBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSupportError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun saveHistory(postDetailBean: PostDetailBean) {
        val historyBean = HistoryBean().apply {
            browserTime = TimeUtil.getLongMs()
            topic_id = postDetailBean.topic.topic_id
            title = postDetailBean.topic.title
            userAvatar = postDetailBean.topic.icon
            user_nick_name = postDetailBean.topic.user_nick_name
            user_id = postDetailBean.topic.user_id
            board_id = postDetailBean.boardId
            board_name = postDetailBean.forumName
            hits = postDetailBean.topic.hits
            replies = postDetailBean.topic.replies
            last_reply_date = postDetailBean.topic.create_date
        }
        for (i in postDetailBean.topic.content.indices) {
            if (postDetailBean.topic.content[i].type == 0) {
                historyBean.subject = postDetailBean.topic.content[i].infor
                break
            }
        }
        historyBean.saveOrUpdate("topic_id = ?", postDetailBean.topic.topic_id.toString())
    }
}