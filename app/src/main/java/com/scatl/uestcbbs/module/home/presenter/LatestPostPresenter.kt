package com.scatl.uestcbbs.module.home.presenter

import com.scatl.uestcbbs.App.Companion.getContext
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.BingPicBean
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.HighLightPostBean
import com.scatl.uestcbbs.entity.NoticeBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.home.model.HomeModel
import com.scatl.uestcbbs.module.home.view.LatestPostView
import com.scatl.uestcbbs.util.BBSLinkUtil.getLinkInfo
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup

/**
 * Created by sca_tl at 2023/6/12 16:46
 */
class LatestPostPresenter: BaseVBPresenter<LatestPostView>() {

    private val homeModel = HomeModel()

    fun getBannerData() {
        homeModel.getBannerData(object : Observer<BingPicBean>() {
            override fun OnSuccess(bingPicBean: BingPicBean) {
                mView?.getBannerDataSuccess(bingPicBean)
            }

            override fun onError(e: ResponseThrowable) {}

            override fun OnCompleted() {}

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getSimplePostList(page: Int, pageSize: Int, sortby: String?) {
        homeModel.getSimplePostList(page, pageSize, 0, sortby,
            object : Observer<CommonPostBean>() {
                override fun OnSuccess(simplePostListBean: CommonPostBean) {
                    if (simplePostListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.getPostListSuccess(simplePostListBean)
                    }
                    if (simplePostListBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.getPostListError(simplePostListBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.getPostListError(e.message)
                }

                override fun OnCompleted() {}

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun getNotice() {
        homeModel.getNotice(object : Observer<NoticeBean>() {
            override fun OnSuccess(noticeBean: NoticeBean) {
                mView?.onGetNoticeSuccess(noticeBean)
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetNoticeError(e.message)
            }

            override fun OnCompleted() {}

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getHighLightPost() {
        homeModel.getHighLightPost(object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)

                    val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    SharePrefUtil.setForumHash(getContext(), formHash)

                    val elements = document.select("div[class=module cl xl xl1]").select("li").select("a[title]")
                    val titles: MutableList<String> = ArrayList()
                    val tids: MutableList<Int> = ArrayList()
                    val highLightPostBean = HighLightPostBean()
                    highLightPostBean.mData = ArrayList()
                    for (i in elements.indices) {
                        if (elements[i].html().contains("<font")) {
                            titles.add(elements[i].select("font").text())
                            tids.add(getLinkInfo(elements[i].attr("href")).id)
                        }
                    }
                    if (titles.size > 0) {
                        for (i in titles.indices) {
                            val data = HighLightPostBean.Data()
                            data.tid = tids[i]
                            data.title = "(" + (i + 1) + "/" + titles.size + ")" + titles[i]
                            highLightPostBean.mData.add(data)
                        }
                    }
                    mView?.onGetHighLightPostSuccess(highLightPostBean)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(e: ResponseThrowable) {}

            override fun OnCompleted() {}

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

}