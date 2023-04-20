package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.RateUserBean

/**
 * Created by sca_tl at 2023/4/20 16:28
 */
interface PingFenView: BaseView {
    fun onGetRateUserSuccess(rateUserBeans: List<RateUserBean>)
    fun onGetRateUserError(msg: String?)
}