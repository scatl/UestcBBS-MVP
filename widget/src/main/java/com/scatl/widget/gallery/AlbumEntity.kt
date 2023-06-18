package com.scatl.widget.gallery

import android.net.Uri

/**
 * Created by sca_tl on 2022/7/22 16:16
 */
data class AlbumEntity(
    var albumName: String = "",

    /**
     * 相册相对路径
     */
    var albumRelativePath: String = "",

    var albumAbsolutePath: String = "",

    var albumId: Int = 0,

    var allMedia: ArrayList<MediaEntity>,

    var selectedMedia: ArrayList<MediaEntity>
)