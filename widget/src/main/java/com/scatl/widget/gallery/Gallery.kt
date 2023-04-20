package com.scatl.widget.gallery

import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import java.lang.ref.WeakReference

/**
 * Created by sca_tl on 2022/7/22 10:50
 */
class Gallery {

    internal var maxSelect = 1
    internal lateinit var context: Context
    internal var selectedMedia = mutableListOf<MediaEntity>()
    internal var mOnMediaSelectedListener: WeakReference<OnMediaSelectedListener>? = null
    internal var mSelectedAlbum: String = ALL_MEDIA_PATH

    companion object {
        internal const val ALL_MEDIA_PATH = "全部图片/"
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Gallery() }
    }

    fun with(context: Context): Gallery {
        reset()
        this.context = context.applicationContext
        return this
    }

    /**
     * 最多可选择的媒体数，最少为1
     */
    fun setMaxSelect(@IntRange(from = 1) maxSelect: Int): Gallery {
        this.maxSelect = maxSelect
        return this
    }

    /**
     * 选择结果回调
     */
    fun setOnMediaSelectedListener(listener: OnMediaSelectedListener): Gallery {
        this.mOnMediaSelectedListener = WeakReference(listener)
        return this
    }

    /**
     * 展示图库
     */
    fun show() {
        context.startActivity(
            Intent(context, PermissionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    internal fun hasMedia(entity: MediaEntity) = selectedMedia.contains(entity)

    internal fun putMedia(entity: MediaEntity) {
        selectedMedia.add(entity)
    }

    internal fun removeMedia(entity: MediaEntity) {
        selectedMedia.remove(entity)
    }

    internal fun isReachMax() = maxSelect == selectedMedia.size

    fun reset() {
        maxSelect = 1
        selectedMedia.clear()
    }

    fun interface OnMediaSelectedListener {
        fun onConfirm(media: MutableList<MediaEntity>)
    }

}