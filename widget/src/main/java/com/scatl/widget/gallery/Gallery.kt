package com.scatl.widget.gallery

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gallery(
    @IntRange(from = 1) var maxSelect: Int = 1,
    var mimeTypes: List<MimeType> = mutableListOf()
) : Parcelable
