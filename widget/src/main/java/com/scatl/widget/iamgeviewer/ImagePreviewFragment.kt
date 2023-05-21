package com.scatl.widget.iamgeviewer

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.scatl.util.ScreenUtil
import com.scatl.widget.databinding.FragmentIamgePreviewBinding
import com.scatl.widget.gallery.MediaEntity
import com.scatl.widget.glide.cache.GlideUtil
import com.scatl.widget.glide.progress.GlideProgressInterceptor
import com.scatl.widget.glide.progress.ProgressListener
import com.scatl.widget.iamgeviewer.dragview.DragListener
import com.scatl.widget.photoview.OnPhotoTapListener
import com.scatl.widget.photoview.PhotoView
import java.io.File

/**
 * Created by sca_tl at 2023/5/8 16:24
 */
class ImagePreviewFragment: Fragment(), View.OnClickListener, OnPhotoTapListener, DragListener {

    private lateinit var mBinding: FragmentIamgePreviewBinding
    private var mediaEntity: MediaEntity? = null
    private var mIsLongImg = false
    private var mIndex = 0

    private val progressListener = object : ProgressListener {
        override fun onProgress(uri: Uri?, progress: Int) {
            (context as Activity).runOnUiThread {
                mBinding.progressBar.isIndeterminate = false
                mBinding.progressBar.progress = progress * 100
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION = 400L

        fun getInstance(bundle: Bundle?) = ImagePreviewFragment().apply { arguments = bundle }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaEntity = arguments?.getSerializable("media") as? MediaEntity
        mIndex = arguments?.getInt("index", 0)?:0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentIamgePreviewBinding.inflate(layoutInflater)
        initView()
        return mBinding.root
    }

    override fun onClick(v: View?) {
        when(v) {
            mBinding.reloadBtn -> {
                mBinding.errorImg.visibility = View.GONE
                mBinding.reloadBtn.visibility = View.GONE
                loadNetImg()
            }
            mBinding.longImageView -> {
                exit()
            }
        }
    }

    override fun onPhotoTap(view: ImageView?, x: Float, y: Float) {
        exit()
    }

    private fun initView() {
        mBinding.photoView.setOnPhotoTapListener(this)
        mBinding.reloadBtn.setOnClickListener(this)
        mBinding.longImageView.setOnClickListener(this)
        mBinding.photoView.setDragListener(this)
        mBinding.longImageView.setDragListener(this)

        mBinding.errorImg.visibility = View.GONE
        mBinding.reloadBtn.visibility = View.GONE

        if (mediaEntity?.isNet == true) {
            loadNetImg()
        } else {
            loadLocalImg(mediaEntity?.uri?.toString())
        }
    }

    private fun loadNetImg() {
        val cacheFile = GlideUtil.getCacheFile(context, mediaEntity?.uri?.toString())
        if (decodeFileAndLoad(cacheFile)) {
            return
        }

        //没有缓存文件，不做动画
        if (!ImageViewer.INSTANCE.mEnterAnimationFlag) {
            ImageViewer.INSTANCE.mEnterAnimationFlag = true
            (context as? IViewerListener)?.onEnter(false)
        }
        mBinding.progressBar.visibility = View.VISIBLE
        GlideProgressInterceptor.LISTENERS[mediaEntity?.uri] = progressListener
        Glide
            .with(this)
            .downloadOnly()
            .load(mediaEntity?.uri)
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    mBinding.errorImg.visibility = View.VISIBLE
                    mBinding.reloadBtn.visibility = View.VISIBLE
                    mBinding.progressBar.visibility = View.GONE
                    GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
                    (context as? IViewerListener)?.onLoadFailed()
                    return true
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    mBinding.errorImg.visibility = View.GONE
                    mBinding.reloadBtn.visibility = View.GONE
                    mBinding.progressBar.visibility = View.GONE
                    GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
                    decodeFileAndLoad(resource)
                    (context as? IViewerListener?)?.onLoadSuccess()
                    return true
                }
            })
            .into(object : FileTarget() { })
    }

    private fun decodeFileAndLoad(file: File?): Boolean {
        if (file?.exists() == true) {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(file.absolutePath, options)

                mediaEntity?.absolutePath = "file://${file.absolutePath}"
                mediaEntity?.height = options.outHeight
                mediaEntity?.width = options.outWidth

                mBinding.progressBar.visibility = View.GONE
                loadLocalImg(mediaEntity?.absolutePath)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return false
    }

    private fun loadLocalImg(path: String?) {
        mBinding.progressBar.visibility = View.GONE
        if ((mediaEntity?.height?:0) > (mediaEntity?.width?:0) * 2.5) {
            mIsLongImg = true
            showLongImage(path)
        } else {
            mIsLongImg = false
            showNormalImage(path)
        }
    }

    private fun showNormalImage(path: String?) {
        mBinding.photoView.visibility = View.VISIBLE
        mBinding.longImageView.visibility = View.GONE
        Glide
            .with(requireContext())
            .load(path)
            .override(ScreenUtil.getScreenWidth(context), ScreenUtil.getScreenHeight(context))
            .into(mBinding.photoView)
        checkShouldDoEnterAnimation(mBinding.photoView)
    }

    private fun showLongImage(path: String?) {
        if (path == null) {
            return
        }
        mBinding.photoView.visibility = View.GONE
        mBinding.longImageView.visibility = View.VISIBLE
        mBinding.longImageView.apply {
            isQuickScaleEnabled = true
            isZoomEnabled = true
            setDoubleTapZoomDuration(100)
            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
            setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
            setImage(ImageSource.uri(path), ImageViewState(0f, PointF(0f, 0f), 0))
        }

        checkShouldDoEnterAnimation(mBinding.longImageView)
    }

    private fun setViewInitState(view: View, rect: Rect) {
        if (view is PhotoView) {
            view.scaleType = ImageView.ScaleType.CENTER_CROP
        } else if (view is SubsamplingScaleImageView) {
            view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
        }
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
            height = rect.height()
            width = rect.width()
        }
        view.translationX = rect.left.toFloat()
        view.translationY = rect.top.toFloat()
        view.scaleX = 1f
        view.scaleY = 1f
    }

    private fun checkShouldDoEnterAnimation(view: View) {
        if (!ImageViewer.INSTANCE.mEnterAnimationFlag) {
            setViewInitState(view, getViewRectF(ImageViewer.INSTANCE.mEnterView))
            view.postDelayed({ doEnterAnimation(view) }, 100)
            ImageViewer.INSTANCE.mEnterAnimationFlag = true
        }
    }

    private fun doEnterAnimation(view: View) {
        TransitionManager.beginDelayedTransition(mBinding.root,
            TransitionSet()
                .setDuration(ANIMATION_DURATION)
                .addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
                .setInterpolator(FastOutSlowInInterpolator())
        )

        if (view is PhotoView) {
            view.scaleType = ImageView.ScaleType.FIT_CENTER
        } else if (view is SubsamplingScaleImageView) {
            view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
        }

        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
            height = ViewGroup.LayoutParams.MATCH_PARENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        view.translationX = 0f
        view.translationY = 0f

        (context as? IViewerListener)?.onEnter(true)
    }

    private fun doExitAnimation(view: View) {
        TransitionManager.beginDelayedTransition(mBinding.root,
            TransitionSet()
                .setDuration(ANIMATION_DURATION)
                .addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
                .setInterpolator(FastOutSlowInInterpolator())
        )
        val exitView = ImageViewer.INSTANCE.getExitView()
        if (exitView == null) {
            val rect = Rect(
                ScreenUtil.getScreenWidth(context) / 2,
                ScreenUtil.getScreenHeight(context) / 2,
                ScreenUtil.getScreenWidth(context) / 2,
                ScreenUtil.getScreenHeight(context) / 2
            )
            setViewInitState(view, rect)
        } else {
            setViewInitState(view, getViewRectF(exitView))
        }
    }

    private fun getViewRectF(view: View?): Rect {
        val loc = intArrayOf(0, 0)
        view?.getLocationOnScreen(loc)
        return Rect(loc[0], loc[1], (view?.width?:0) + loc[0], (view?.height?:0) + loc[1])
    }

    internal fun exit() {
        if (mBinding.errorImg.visibility == View.VISIBLE || mBinding.progressBar.visibility == View.VISIBLE) {
            (context as? IViewerListener?)?.onExit(false)
            return
        }
        if (mIsLongImg) {
            doExitAnimation(mBinding.longImageView)
        } else {
            doExitAnimation(mBinding.photoView)
        }
        (context as? IViewerListener?)?.onExit(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
    }

    override fun onDragging(view: View, fraction: Float) {
        (context as? IViewerListener)?.onDragging(fraction)
    }

    override fun onRestoring(view: View, fraction: Float) {
        (context as? IViewerListener)?.onDragRestoring(fraction)
    }

    override fun onRelease(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({ exit() }, 20)
    }

    override fun onSetViewPagerInputEnable(value: Boolean) {
        (context as? IViewerListener)?.onSetViewPagerInputEnable(value)
    }
}