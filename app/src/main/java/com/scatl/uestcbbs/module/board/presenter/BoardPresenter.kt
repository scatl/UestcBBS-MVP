package com.scatl.uestcbbs.module.board.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.ForumDetailBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.board.model.BoardModel
import com.scatl.uestcbbs.module.board.view.BoardView
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/4/27 10:35
 */
class BoardPresenter: BaseVBPresenter<BoardView>() {

    private val boardModel = BoardModel()

    fun getSubBoardList(fid: Int) {
        boardModel.getSubForumList(fid, object : Observer<SubForumListBean>() {
            override fun OnSuccess(subForumListBean: SubForumListBean) {
                if (subForumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    mView?.onGetSubBoardListSuccess(subForumListBean)
                }
                if (subForumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onGetSubBoardListError(subForumListBean.head.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetSubBoardListError(e.message)
            }

            override fun OnCompleted() {}
            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getForumDetail(fid: Int) {
        boardModel.getForumDetail(fid, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val formhash = document.select("div[class=hdc]").select("div[class=wp]").select("div[class=cl]").select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    SharePrefUtil.setForumHash(mView?.getContext(), formhash)

                    val forumDetailBean = ForumDetailBean().apply {
                        admins = mutableListOf()
                    }

                    val hasAdminInfo = document.select("div[class=bm_c cl pbn]").select("div")?.getOrNull(1)?.ownText()?.contains("版主:")
                    if (hasAdminInfo == true) {
                        val adminHtml = document.select("div[class=bm_c cl pbn]").select("div")[1].select("span[class=xi2]").select("a")
                        adminHtml.forEach {
                            val bean = ForumDetailBean.Admin()
                            bean.name = it.ownText()
                            bean.uid = BBSLinkUtil.getLinkInfo(it.attr("href")).id
                            bean.avatar = Constant.USER_AVATAR_URL.plus(bean.uid)
                            forumDetailBean.admins.add(bean)
                        }
                    }
                    mView?.onGetForumDetailSuccess(forumDetailBean)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(e: ResponseThrowable) {

            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}