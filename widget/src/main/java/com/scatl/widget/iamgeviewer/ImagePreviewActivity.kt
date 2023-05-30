package com.scatl.widget.iamgeviewer

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.scatl.util.ColorUtil
import com.scatl.util.FileUtil
import com.scatl.util.ScreenUtil
import com.scatl.util.SystemUtil
import com.scatl.util.desensitize
import com.scatl.widget.databinding.ActivityImagePreviewBinding
import com.scatl.widget.gallery.MediaEntity
import com.scatl.widget.glide.cache.GlideUtil
import java.io.File
import java.io.FileInputStream

/**
 * created by sca_tl at 2022/6/10 19:46
 */
@SuppressLint("SetTextI18n")
class ImagePreviewActivity: AppCompatActivity(), View.OnClickListener, IViewerListener {

    private var medias: MutableList<MediaEntity>? = null
    private lateinit var imagePreviewPagerAdapter: ImagePreviewPagerAdapter
    private lateinit var mBinding: ActivityImagePreviewBinding
    private var enterIndex = 0
    private var currentIndex = 0
    private var currentAlpha = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar
            .with(this)
            .transparentNavigationBar()
            .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            .init()
        super.onCreate(savedInstanceState)
        mBinding = ActivityImagePreviewBinding.inflate(layoutInflater)

        medias = intent?.getParcelableArrayListExtra("media")
        enterIndex = intent?.getIntExtra(ImageConstant.ENTER_INDEX, 0)?:0
        currentIndex = enterIndex

        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
//        postponeEnterTransition()
        mBinding.saveBtn.setOnClickListener(this)

        mBinding.indicator.text = "${currentIndex + 1} / ${medias?.size}"
        imagePreviewPagerAdapter = ImagePreviewPagerAdapter(this, medias)
        mBinding.viewPager.adapter = imagePreviewPagerAdapter
        mBinding.viewPager.desensitize()
        mBinding.viewPager.setCurrentItem(currentIndex, false)
        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                ImageViewer.INSTANCE.mExitIndex = position
                currentIndex = position
                mBinding.indicator.text = "${position + 1} / ${medias?.size}"
                mBinding.saveBtn.visibility = if (medias?.getOrNull(position)?.isNet == true) View.VISIBLE else View.GONE
            }
        })

        val naviBarH = ImmersionBar.getNavigationBarHeight(this)
        mBinding.saveBtn.layoutParams = (mBinding.saveBtn.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = ScreenUtil.dip2px(this@ImagePreviewActivity, 20f) + naviBarH
        }

//        Handler(Looper.getMainLooper()).postDelayed({ startPostponedEnterTransition() }, 80)
    }

    override fun onClick(v: View?) {
        when(v) {
            mBinding.saveBtn -> {
                if ((SystemUtil.isHarmonyOs() && SystemUtil.getHarmonyVersionCode() < 6) || Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    if (!hasPermission()) {
                        requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
                    } else {
                        saveNetImg()
                    }
                } else {
                    saveNetImg()
                }
            }
        }
    }

    override fun finishAfterTransition() {
//        ImageViewer.INSTANCE.exit(currentIndex)
        super.finishAfterTransition()
    }

    override fun onEnter(animation: Boolean) {
        if (animation) {
            onSetViewPagerInputEnable(false)
            ValueAnimator
                .ofFloat(currentAlpha, 1f)
                .setDuration(ImagePreviewFragment.ANIMATION_DURATION)
                .apply {
                    addUpdateListener {
                        val v = it.animatedValue as Float
                        mBinding.root.setBackgroundColor(ColorUtil.getAlphaColor(v, Color.BLACK))
                        mBinding.saveBtn.alpha = v
                        mBinding.saveBtn.visibility = if (medias?.getOrNull(enterIndex)?.isNet == true) View.VISIBLE else View.GONE
                        mBinding.indicator.alpha = v
                        currentAlpha = v
                        onSetViewPagerInputEnable(v == 1f)
                    }
                    start()
                }
        } else {
            currentAlpha = 1f
            mBinding.root.setBackgroundColor(Color.BLACK)
            mBinding.saveBtn.alpha = 1f
            mBinding.saveBtn.visibility = if (medias?.getOrNull(enterIndex)?.isNet == true) View.VISIBLE else View.GONE
            mBinding.indicator.alpha = 1f
        }
    }

    override fun onDragging(fraction: Float) {
        mBinding.root.setBackgroundColor(ColorUtil.getAlphaColor(1 - fraction, Color.BLACK))
        mBinding.saveBtn.alpha = 1 - fraction
        mBinding.indicator.alpha = 1 - fraction
        currentAlpha = 1 - fraction
    }

    override fun onDragRestoring(fraction: Float) {
        mBinding.root.setBackgroundColor(ColorUtil.getAlphaColor(1 - fraction, Color.BLACK))
        mBinding.saveBtn.alpha = 1 - fraction
        mBinding.indicator.alpha = 1 - fraction
        currentAlpha = 1 - fraction
    }

    override fun onExit(animation: Boolean) {
        if (animation) {
            ValueAnimator
                .ofFloat(currentAlpha, 0f)
                .setDuration(ImagePreviewFragment.ANIMATION_DURATION)
                .apply {
                    addUpdateListener {
                        val v = it.animatedValue as Float
                        mBinding.root.setBackgroundColor(ColorUtil.getAlphaColor(v, Color.BLACK))
                        mBinding.saveBtn.alpha = v
                        mBinding.indicator.alpha = v
                        currentAlpha = v
                        if (v <= 0) {
                            overridePendingTransition(0, 0)
                            finish()
                        }
                    }
                    start()
                }
        } else {
            overridePendingTransition(0, 0)
            finish()
        }
        ImageViewer.INSTANCE.exit()
    }

    override fun onLoadFailed() {
        mBinding.saveBtn.alpha = 0f
    }

    override fun onLoadSuccess() {

    }

    override fun onSetViewPagerInputEnable(value: Boolean) {
        mBinding.viewPager.isUserInputEnabled = value
    }

    override fun onFinish() {
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onBackPressed() {
        getCurrentFragment()?.exit()
    }

    private fun getCurrentFragment(): ImagePreviewFragment? {
        val tag = "f" + imagePreviewPagerAdapter.getItemId(mBinding.viewPager.currentItem)
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        return (fragment as? ImagePreviewFragment?)
    }

    private fun saveNetImg() {
        val cacheFile = GlideUtil.getCacheFile(this, medias?.getOrNull(currentIndex)?.uri?.toString())
        if (cacheFile != null && cacheFile.exists()) {
            FileUtil.saveImgFileToGallery(this, cacheFile, ImageViewer.INSTANCE.mSavePath)
            return
        }

        Glide
            .with(this)
            .downloadOnly()
            .load(medias?.getOrNull(currentIndex)?.uri)
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    FileUtil.saveImgFileToGallery(this@ImagePreviewActivity, resource, ImageViewer.INSTANCE.mSavePath)
                    return true
                }
            })
            .into(object : FileTarget() {})
    }

    private fun hasPermission() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        var grantedAll = true
        result.entries.forEach {
            grantedAll = grantedAll and it.value
        }
        if (grantedAll) {
            saveNetImg()
        }
    }
}