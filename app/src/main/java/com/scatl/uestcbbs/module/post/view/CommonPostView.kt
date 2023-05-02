package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.CommonPostBean

/**
 * Created by sca_tl at 2023/4/26 10:09
 */
interface CommonPostView: BaseView {
    fun onGetPostSuccess(commonPostBean: CommonPostBean)
    fun onGetPostError(msg: String?)
}