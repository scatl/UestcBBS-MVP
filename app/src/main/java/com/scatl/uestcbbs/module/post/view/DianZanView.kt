package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.PostDetailBean

/**
 * Created by sca_tl at 2023/4/13 14:08
 */
interface DianZanView: BaseView {
    fun onGetPostDetailSuccess(postDetailBean: PostDetailBean)
    fun onGetPostDetailError(msg: String?)
}