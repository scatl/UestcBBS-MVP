package com.scatl.widget.iamgeviewer

import android.app.Activity
import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.scatl.widget.R
import com.scatl.widget.gallery.MediaEntity
import java.lang.ref.WeakReference

/**
 * created by sca_tl at 2022/6/11 11:34
 */
class ImageViewer {

    internal var mEnterIndex: Int = 0
    internal var mExitIndex: Int = 0
    internal var mEnterView: View? = null
    internal var mSavePath: String? = null
    internal var mViewChangeListener: WeakReference<ViewChangeListener>? = null
    internal lateinit var mMediaEntity: List<MediaEntity>
    private var weakContext: WeakReference<Context>? = null

    /**
     * 是否已经播放了进入动画
     */
    internal var mEnterAnimationFlag = false

    companion object {
        private const val TAG = "ImageViewer"
        val INSTANCE: ImageViewer by lazy ( mode = LazyThreadSafetyMode.SYNCHRONIZED ) {
            ImageViewer()
        }
    }

    fun setEnterIndex(index: Int) = apply {
        mEnterIndex = index
    }

    fun setEnterView(view: View?) = apply {
        mEnterView = view
    }

    fun setMediaEntity(entities: List<MediaEntity>) = apply {
        mMediaEntity = entities
    }

    fun setSavePath(path: String?) = apply {
        mSavePath = path
    }

    fun with(context: Context?) = apply {
        weakContext = WeakReference<Context>(context)
    }

    fun show() {
        mExitIndex = mEnterIndex
        if (mSavePath.isNullOrEmpty()) {
            mSavePath = weakContext?.get()?.applicationInfo?.packageName
        }
        val intent = Intent(weakContext?.get(), ImagePreviewActivity::class.java)
        weakContext?.get()?.startActivity(intent)
    }

    fun exit() {
        mExitIndex = 0
        mEnterAnimationFlag = false
        mMediaEntity = listOf()
    }

    internal fun getExitView() = mViewChangeListener?.get()?.onViewChanged(mExitIndex)

    /**
     * 当图片位置发生变化时，需要更新源图片，这样退出时动画能够正常
     */
    fun setViewChangeListener(listener: ViewChangeListener) = apply {
        this.mViewChangeListener = WeakReference<ViewChangeListener>(listener)
    }

    fun interface ViewChangeListener {
        fun onViewChanged(currentPosition: Int): View?
    }

}