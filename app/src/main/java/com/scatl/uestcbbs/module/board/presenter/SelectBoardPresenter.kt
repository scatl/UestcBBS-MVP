package com.scatl.uestcbbs.module.board.presenter

import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.entity.SingleBoardBean
import com.scatl.uestcbbs.entity.SingleBoardBean.ClassificationTypeListBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.board.model.BoardModel
import com.scatl.uestcbbs.module.board.view.SelectBoardView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by sca_tl at 2023/3/31 10:51
 */
class SelectBoardPresenter: BaseVBPresenter<SelectBoardView>() {

    private val boardModel = BoardModel()

    fun getMainForumList() {
        boardModel.getForumList(object : Observer<ForumListBean>() {
            override fun OnSuccess(forumListBean: ForumListBean) {
                if (forumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    mView?.onGetMainBoardListSuccess(forumListBean)
                } else if (forumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onGetMainBoardListError(forumListBean.head.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetMainBoardListError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getChildBoardList(fid: Int, fatherBoardName: String) {
        boardModel.getSubForumList(fid, object : Observer<SubForumListBean>() {
            override fun OnSuccess(subForumListBean: SubForumListBean) {
                if (subForumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    val slb = SubForumListBean.ListBean.BoardListBean()
                    slb.board_name = fatherBoardName
                    slb.board_id = fid
                    if (subForumListBean.list == null || subForumListBean.list.size == 0) {
                        val list: MutableList<SubForumListBean.ListBean> = ArrayList()
                        val sl = SubForumListBean.ListBean()
                        sl.board_list = ArrayList()
                        sl.board_list.add(0, slb)
                        list.add(sl)
                        subForumListBean.list = list
                    } else {
                        subForumListBean.list[0].board_list.add(0, slb)
                    }
                    mView?.onGetChildBoardListSuccess(subForumListBean)
                } else if (subForumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onGetChildBoardListError(subForumListBean.head.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetChildBoardListError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getClassification(boardId: Int) {
        boardModel.getSingleBoardPostList(1, 0, 1, boardId, 0, "typeid", "new",
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(singleBoardBean: CommonPostBean) {
                    val sc = CommonPostBean.ClassificationTypeListBean()
                    sc.classificationType_name = "不分类"
                    sc.classificationType_id = 0

                    if (singleBoardBean.classificationTop_list.isNullOrEmpty()) {
                        singleBoardBean.classificationTop_list = mutableListOf<SingleBoardBean.ClassificationTypeListBean>()
                    }
                    singleBoardBean.classificationType_list.add(0, sc)

                    if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetClassificationSuccess(singleBoardBean.classificationType_list)
                    } else if (singleBoardBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetClassificationError(singleBoardBean.head.errInfo, singleBoardBean.classificationType_list)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    val list = mutableListOf<CommonPostBean.ClassificationTypeListBean>()
                    val sc = CommonPostBean.ClassificationTypeListBean()
                    sc.classificationType_name = "不分类"
                    sc.classificationType_id = 0
                    list.add(sc)
                    mView?.onGetClassificationError(e.message, list)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }
}