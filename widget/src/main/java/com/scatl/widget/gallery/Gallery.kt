package com.scatl.widget.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import com.scatl.widget.iamgeviewer.ImageConstant

/**
 * Created by sca_tl on 2022/7/22 10:50
 */
class Gallery {

    internal var maxSelect = Int.MAX_VALUE
    internal var mShowGif = true
    internal var mRequestCode = ImageConstant.RESULT_CODE
    internal var selectedMedia = mutableListOf<MediaEntity>()
    internal var mSelectedAlbum: String? = ImageConstant.ALL_MEDIA_PATH
    internal var context: Context? = null

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Gallery() }
    }

    fun with(context: Context?) = apply {
        this.context = context
    }

    fun setShowGif(showGif: Boolean) = apply {
        mShowGif = showGif
    }

    fun setMaxSelect(@IntRange(from = 1) maxSelect: Int) = apply {
        this.maxSelect = maxSelect
    }

    fun show(requestCode: Int = ImageConstant.RESULT_CODE) {
        mRequestCode = requestCode
        reset()
        (context as? Activity?)?.startActivityForResult(
            Intent(context, GalleryActivity::class.java), requestCode
        )
    }

    internal fun isReachMaxSelect() = maxSelect == selectedMedia.size

    internal fun reset() {
        maxSelect = Int.MAX_VALUE
        selectedMedia.clear()
        mShowGif = true
    }
}