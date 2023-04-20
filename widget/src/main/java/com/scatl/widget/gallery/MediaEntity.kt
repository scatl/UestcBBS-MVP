package com.scatl.widget.gallery

import android.net.Uri

/**
 * Created by sca_tl on 2022/7/20 19:43
 */
data class MediaEntity (
    var id: Long,
    var absolutePath: String,
    var relativePath: String,
    var name: String,
    var modifyDate: Long,
    var uri: Uri,
    var albumName: String
)