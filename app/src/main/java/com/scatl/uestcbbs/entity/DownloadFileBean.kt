package com.scatl.uestcbbs.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * author: sca_tl
 * date: 2022/1/8 13:07
 * description:
 */
data class DownloadFileBean(
        @Column(index = true)
        var id: Int = 0,

        var url: String = "",
        var name: String = "",
        var progress: Int = 0,
        var done: Boolean = false
): LitePalSupport(), Serializable
