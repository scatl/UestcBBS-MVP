package com.scatl.widget.gallery

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Created by sca_tl on 2022/7/20 19:43
 */
@Parcelize
data class MediaEntity (
    var id: Long? = -1,
    var absolutePath: String? = "",
    var relativePath: String? = "",
    var name: String? = "",
    var modifyDate: Long? = -1,
    var uri: Uri? = null,
    var albumName: String? = "",
    var mimeType: String? = "",
    var width: Int? = -1,
    var height: Int? = -1,
    var isGif: Boolean = false,
    var isHeic: Boolean = false,
    var isWebp: Boolean = false,
    var isNet: Boolean = false
): Serializable, Parcelable