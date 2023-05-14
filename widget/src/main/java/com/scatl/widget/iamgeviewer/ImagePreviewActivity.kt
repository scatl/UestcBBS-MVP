package com.scatl.widget.iamgeviewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.scatl.widget.databinding.ActivityImagePreviewBinding
import com.scatl.widget.gallery.MediaEntity
import com.scatl.widget.gallery.desensitize

/**
 * created by sca_tl at 2022/6/10 19:46
 */
@SuppressLint("SetTextI18n")
class ImagePreviewActivity: AppCompatActivity() {

    private var medias: MutableList<MediaEntity>? = null
    private lateinit var imagePreviewPagerAdapter: ImagePreviewPagerAdapter
    private lateinit var mBinding: ActivityImagePreviewBinding
    private var enterIndex = 0
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init()
        super.onCreate(savedInstanceState)
        mBinding = ActivityImagePreviewBinding.inflate(layoutInflater)

        medias = intent?.getParcelableArrayListExtra("media")
        enterIndex = intent?.getIntExtra(ImageConstant.ENTER_INDEX, 0)?:0
        currentIndex = enterIndex

        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        postponeEnterTransition()

        mBinding.indicator.text = "${currentIndex + 1}/${medias?.size}"
        imagePreviewPagerAdapter = ImagePreviewPagerAdapter(this, medias)
        mBinding.viewPager.adapter = imagePreviewPagerAdapter
        mBinding.viewPager.desensitize()
        mBinding.viewPager.setCurrentItem(currentIndex, false)
        mBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentIndex = position
                mBinding.indicator.text = "${position + 1}/${medias?.size}"
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({ startPostponedEnterTransition() }, 80)
    }

    override fun finishAfterTransition() {
        ImageViewer.INSTANCE.exit(currentIndex)
        super.finishAfterTransition()
    }

}