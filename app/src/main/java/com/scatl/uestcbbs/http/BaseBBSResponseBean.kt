package com.scatl.uestcbbs.http

import com.scatl.uestcbbs.entity.AtMsgBean

/**
 * Created by sca_tl at 2023/6/8 17:18
 */
open class BaseBBSResponseBean {
    var rs = 0
    var errcode: String? = null
    var head: HeadBean? = null

    class HeadBean {
        var errCode: String? = null
        var errInfo: String? = null
        var version: String? = null
        var alert = 0
    }

    fun success() = rs == 1
    fun message() = head?.errInfo
}