package com.scatl.uestcbbs.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * author: sca_tl
 * date: 2022/1/15 12:46
 * description: 赞过的帖子pid
 */
data class SupportedBean(
        @Column(index = true)
        public var id: Int = 0,

        public var pid: Int = 0
): LitePalSupport(), Serializable
