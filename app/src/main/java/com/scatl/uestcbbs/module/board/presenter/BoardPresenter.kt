package com.scatl.uestcbbs.module.board.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.ForumDetailBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.board.model.BoardModel
import com.scatl.uestcbbs.module.board.view.BoardView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by tanlei02 at 2023/4/27 10:35
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
                    val forumDetailBean = ForumDetailBean()
                    val ee = document.select("div[class=bm_c cl pbn]").select("span[class=xi2]")
                    if (!ee.isEmpty()) {
                        forumDetailBean.admins = ee[0].select("a").eachText()
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