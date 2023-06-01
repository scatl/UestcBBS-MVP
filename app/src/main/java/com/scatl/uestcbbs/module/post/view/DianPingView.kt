package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.PostDianPingBean

/**
 * Created by sca_tl at 2023/4/13 9:32
 */
interface DianPingView: BaseView {
    fun onGetPostDianPingListSuccess(commentBean: PostDianPingBean)
    fun onGetPostDianPingListError(msg: String?)
}