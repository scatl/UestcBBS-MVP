package com.scatl.uestcbbs.module.darkroom.model

import com.scatl.uestcbbs.util.RetrofitUtil
import io.reactivex.Observable

/**
 * Created by sca_tl at 2023/6/6 15:33
 */
class DarkRoomModel {
    fun getDarkRoomList(): Observable<String> {
        return RetrofitUtil
            .getInstance()
            .apiService
            .darkRoomList
    }

}