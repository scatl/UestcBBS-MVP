package com.scatl.uestcbbs.module.home.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.BingPicBean
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.HighLightPostBean
import com.scatl.uestcbbs.entity.NoticeBean

/**
 * Created by sca_tl at 2023/6/12 16:46
 */
interface LatestPostView: BaseView {
    fun getBannerDataSuccess(bingPicBean: BingPicBean)
    fun getPostListSuccess(postListBean: CommonPostBean)
    fun getPostListError(msg: String?)
    fun onGetNoticeSuccess(noticeBean: NoticeBean)
    fun onGetHighLightPostSuccess(highLightPostBean: HighLightPostBean)
}