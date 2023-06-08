package com.scatl.uestcbbs.module.setting.presenter

import android.app.Activity
import com.alibaba.fastjson.JSONObject
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.OpenSourceBean
import com.scatl.uestcbbs.module.setting.view.OpenSourceView
import com.scatl.util.FileUtil
import kotlin.concurrent.thread

/**
 * Created by sca_tl at 2023/6/6 16:17
 */
class OpenSourcePresenter: BaseVBPresenter<OpenSourceView>() {

    fun getOpenSourceData() {
        thread {
            val data = FileUtil.readAssetFileToString(mView?.getContext(), "open_source_projects.json")
            try {
                val openSourceBeanList = JSONObject.parseArray(data, OpenSourceBean::class.java)
                (mView?.getContext() as Activity).runOnUiThread {
                    mView?.onGetOpenSourceDataSuccess(openSourceBeanList)
                }
            } catch (e: Exception) {
                (mView?.getContext() as Activity).runOnUiThread {
                    mView?.onGetOpenSourceDataError(e.message)
                }
            }
        }
    }

}