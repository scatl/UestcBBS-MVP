package com.scatl.widget.gallery

import android.net.Uri

/**
 * Created by sca_tl on 2022/7/22 16:16
 */
data class AlbumEntity(
    var albumName: String? = "",

    /**
     * 相册绝对路径
     */
    var albumPath: String? = "",

    var coverImage: Uri? = null,
    var allMedia: ArrayList<MediaEntity>,

    var selectedMedia: ArrayList<MediaEntity>
)