package com.scatl.uestcbbs.module.darkroom.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.DarkRoomBean

/**
 * Created by sca_tl at 2023/6/6 15:32
 */
interface DarkRoomView: BaseView {
    fun onGetDarkRoomDataSuccess(darkRoomBeanList: List<DarkRoomBean>)
    fun onGetDarkRoomDataError(msg: String?)
}