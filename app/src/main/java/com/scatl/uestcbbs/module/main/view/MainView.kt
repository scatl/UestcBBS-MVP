package com.scatl.uestcbbs.module.main.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.SettingsBean
import com.scatl.uestcbbs.entity.UpdateBean

/**
 * Created by sca_tl at 2023/4/11 17:23
 */
interface MainView: BaseView {
    fun getUpdateSuccess(updateBean: UpdateBean)
    fun getSettingsSuccess(settingsBean: SettingsBean)
}