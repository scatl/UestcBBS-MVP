package com.scatl.uestcbbs.module.board.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.board.model.BoardModel
import com.scatl.uestcbbs.module.board.view.BoardPostView
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * created by sca_tl at 2023/6/10 12:07
 */
class BoardPostPresenter: BaseVBPresenter<BoardPostView>() {

    private val boardModel = BoardModel()

    fun getBoardPostList(page: Int,
                         pageSize: Int,
                         topOrder: Int,
                         boardId: Int,
                         filterId: Int,
                         filterType: String,
                         sortby: String) {
        boardModel.getBoardPostList(page, pageSize,
            topOrder, boardId, filterId, filterType, sortby,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(singleBoardBean: CommonPostBean) {
                    if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetBoardPostSuccess(singleBoardBean)
                    }
                    if (singleBoardBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetBoardPostError(singleBoardBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onGetBoardPostError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun payForVisiting(fid: Int, formhash: String?) {
        boardModel.payForVisiting(fid, formhash, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (s.contains("支付成功")) {
                    mView?.onPaySuccess("支付成功。请【返回】或者【重新登录】再进入该页面，可能需要稍等一会才能浏览！")
                } else {
                    try {
                        val document = Jsoup.parse(s)
                        val info = document.select("div[id=messagetext]").text()
                        mView?.onPayError("支付失败:$info")
                    } catch (e: Exception) {
                        mView?.onPayError("支付失败:" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onPayError("支付失败：" + e.message)
            }

            override fun OnCompleted() {}
            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }
}