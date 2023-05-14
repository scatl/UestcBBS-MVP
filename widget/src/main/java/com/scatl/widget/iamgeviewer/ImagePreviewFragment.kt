package com.scatl.widget.iamgeviewer

import android.app.Activity
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
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
import com.scatl.widget.databinding.FragmentIamgePreviewBinding
import com.scatl.widget.gallery.MediaEntity
import com.scatl.widget.glideprogress.GlideProgressInterceptor
import com.scatl.widget.glideprogress.ProgressListener
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference

/**
 * Created by sca_tl at 2023/5/8 16:24
 */
class ImagePreviewFragment: Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentIamgePreviewBinding
    private var mediaEntity: MediaEntity? = null

    private val progressListener = WeakReference<ProgressListener>(object : ProgressListener {
        override fun onProgress(progress: Int) {
            (context as Activity).runOnUiThread {
                mBinding.progressBar.isIndeterminate = false
                mBinding.progressBar.progress = progress * 100
            }
        }
    })

    companion object {
        fun getInstance(bundle: Bundle?) = ImagePreviewFragment().apply { arguments = bundle }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaEntity = arguments?.getSerializable("media") as? MediaEntity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentIamgePreviewBinding.inflate(layoutInflater)
        initView()
        return mBinding.root
    }

    override fun onClick(v: View?) {
        when(v) {
            mBinding.reloadBtn -> {
                loadNetImg()
            }
            mBinding.saveBtn -> {
                saveNetImg()
            }
        }
    }

    private fun initView() {
        mBinding.reloadBtn.setOnClickListener(this)
        mBinding.saveBtn.setOnClickListener(this)
        mBinding.errorImg.visibility = View.GONE
        mBinding.reloadBtn.visibility = View.GONE

        if (mediaEntity?.isNet == true) {
            mBinding.progressBar.visibility = View.VISIBLE
            mBinding.saveBtn.visibility = View.GONE
            loadNetImg()
        } else {
            mBinding.saveBtn.visibility = View.GONE
            loadImg(mediaEntity?.uri?.toString())
        }
    }

    private fun loadNetImg() {
        GlideProgressInterceptor.LISTENERS[mediaEntity?.uri] = progressListener
        Glide
            .with(this)
            .downloadOnly()
            .load(mediaEntity?.uri)
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    mBinding.errorImg.visibility = View.VISIBLE
                    mBinding.reloadBtn.visibility = View.VISIBLE
                    mBinding.saveBtn.visibility = View.GONE
                    mBinding.progressBar.visibility = View.GONE
                    GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
                    return true
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    mBinding.errorImg.visibility = View.GONE
                    mBinding.reloadBtn.visibility = View.GONE
                    mBinding.saveBtn.visibility = View.VISIBLE
                    GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
                    if (resource?.exists() == true) {
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeFile(resource.absolutePath, options)

                        mediaEntity?.absolutePath = "file://${resource.absolutePath}"
                        mediaEntity?.height = options.outHeight
                        mediaEntity?.width = options.outWidth

                        mBinding.progressBar.visibility = View.GONE
                        loadImg(mediaEntity?.absolutePath)
                    }
                    return true
                }
            })
            .into(object : FileTarget() {})
    }

    private fun saveNetImg() {
        Glide
            .with(this)
            .downloadOnly()
            .load(mediaEntity?.uri)
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    val fileName = System.currentTimeMillis().toString()
                    var extension = "jpg"

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(FileInputStream(resource), null, options)

                    if (options.outMimeType?.startsWith("image/") == true) {
                        extension = options.outMimeType.replace("image/", "")
                    }

                    try {
                        FileInputStream(resource).use { inputStream ->
                            val uri = context!!.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName.plus(".").plus(extension))
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/${extension}")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/${ImageViewer.INSTANCE.mSavePath}")
                                    } else {
                                        val path = Environment.getExternalStorageDirectory().toString().plus(File.separator).plus(Environment.DIRECTORY_PICTURES)
                                            .plus(File.separator).plus(fileName).plus(".").plus(extension)
                                        put(MediaStore.MediaColumns.DATA, path)
                                    }
                                }
                            )

                            context?.contentResolver?.openOutputStream(uri!!)?.use { outputStream ->
                                val buffer = ByteArray(4096)
                                var len: Int
                                do {
                                    len = inputStream.read(buffer)
                                    if (len != -1) {
                                        outputStream.write(buffer, 0, len)
                                        outputStream.flush()
                                    }
                                } while (len != -1)
                            }

                            Toast.makeText(context, "成功保存到相册：Pictures/${ImageViewer.INSTANCE.mSavePath}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "保存失败:${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
            })
            .into(object : FileTarget() {})
    }

    private fun loadImg(path: String?) {
        if ((mediaEntity?.height?:0) > (mediaEntity?.width?:0) * 2.5) {
            showLongImage(path)
        } else {
            showNormalImage(path)
        }
    }

    private fun showNormalImage(path: String?) {
        mBinding.photoView.visibility = View.VISIBLE
        mBinding.longImageView.visibility = View.GONE
        Glide.with(requireContext()).load(path).into(mBinding.photoView)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        GlideProgressInterceptor.LISTENERS.remove(mediaEntity?.uri)
    }
}