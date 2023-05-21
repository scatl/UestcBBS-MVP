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
    internal var mSavePath: String? = "images"
    internal var mViewChangeListener: ViewChangeListener? = null
    internal lateinit var mMediaEntity: List<MediaEntity>
    private lateinit var weakContext: WeakReference<Context>

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
        val intent = Intent(weakContext.get(), ImagePreviewActivity::class.java).apply {
            putParcelableArrayListExtra("media", mMediaEntity as ArrayList<MediaEntity>)
            putExtra(ImageConstant.ENTER_INDEX, mEnterIndex)
        }
//        if (mEnterView == null) {
            weakContext.get()?.startActivity(intent)
//        } else {
//            val bundle = ActivityOptions.makeSceneTransitionAnimation(weakContext.get() as Activity, mEnterView,
//                weakContext.get()?.getString(R.string.image_preview_transition_name)).toBundle()
//            weakContext.get()?.startActivity(intent, bundle)
//        }
    }

    fun exit(exitIndex: Int = mEnterIndex) {
        mExitIndex = exitIndex
        mEnterAnimationFlag = false
//        val exitView: View = getExitView() ?: return
//        (weakContext.get() as? Activity?)?.setExitSharedElementCallback(object: SharedElementCallback() {
//            override fun onMapSharedElements(names: MutableList<String?>, sharedElements: MutableMap<String?, View?>) {
//                names.clear()
//                sharedElements.clear()
//                names.add(ViewCompat.getTransitionName(exitView))
//                sharedElements[ViewCompat.getTransitionName(exitView)] = exitView
//                (weakContext.get() as? Activity?)?.setExitSharedElementCallback(object: SharedElementCallback() { })
//            }
//        })
    }

    internal fun getExitView() = mViewChangeListener?.onViewChanged(mExitIndex)

    /**
     * 当图片位置发生变化时，需要更新源图片，这样退出时动画能够正常
     */
    fun setViewChangeListener(listener: ViewChangeListener) = apply {
        this.mViewChangeListener = listener
    }

    fun interface ViewChangeListener {
        fun onViewChanged(currentPosition: Int): View?
    }

}