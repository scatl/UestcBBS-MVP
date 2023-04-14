package com.scatl.uestcbbs.module.main.presenter

import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.SettingsBean
import com.scatl.uestcbbs.entity.UpdateBean
import com.scatl.uestcbbs.module.main.model.MainModel
import com.scatl.uestcbbs.module.main.view.MainView
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/4/11 17:23
 */
class MainPresenter: BaseVBPresenter<MainView>() {

    private val mainModel = MainModel()

    fun getUpdate(oldVersionCode: Int, isTest: Boolean) {
        mainModel
            .getUpdate(oldVersionCode, isTest)
            .subscribeEx(com.scatl.uestcbbs.http.Observer<UpdateBean>().observer {
                onSuccess {
                    mView?.getUpdateSuccess(it)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun getSettings() {
        mainModel
            .getSettings()
            .subscribeEx(com.scatl.uestcbbs.http.Observer<SettingsBean>().observer {
                onSuccess {
                    mView?.getSettingsSuccess(it)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

}