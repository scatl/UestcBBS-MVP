package com.scatl.uestcbbs.helper

import com.scatl.uestcbbs.entity.BlackListBean

/**
 * Created by sca_tl at 2023/4/11 16:54
 */
class BlackListManager {

    val blackList = mutableMapOf<String, List<BlackListBean>>()

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BlackListManager()
        }
    }

    fun init() {

    }

}