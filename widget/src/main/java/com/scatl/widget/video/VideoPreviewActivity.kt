package com.scatl.widget.video

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.scatl.widget.R
import com.scatl.widget.databinding.ActivityVideoPreviewBinding
import com.scatl.widget.download.DownloadManager
import xyz.doikki.videocontroller.StandardVideoController

/**
 * Created by sca_tl at 2023/3/2 15:59
 */
class VideoPreviewActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityVideoPreviewBinding
    private var mUrl: String? = ""
    private var mName: String? = ""
    private var mCookies: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityVideoPreviewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ImmersionBar.with(this).statusBarColorInt(Color.TRANSPARENT).init()

        mName = intent.getStringExtra("name")
        mUrl = intent.getStringExtra("url")
        mCookies = intent.getStringExtra("cookies")

        initView()
        initData()
    }

    private fun initView() {
        mBinding.toolbar.apply {
            setNavigationOnClickListener { v: View? -> finish() }
            setOnMenuItemClickListener { item: MenuItem? ->
                when(item?.itemId) {
                    R.id.download -> {
                        DownloadManager
                            .with(this@VideoPreviewActivity)
                            .setUrl(mUrl)
                            .setName(mName)
                            .setCookies(mCookies)
                            .start()
                    }
                }
                true
            }
        }
    }

    private fun initData() {
        if (mUrl != null) {
            val headers = mapOf(
                "Cookie" to mCookies
            )
            mBinding.videoView.setUrl(mUrl, headers)
            val controller = StandardVideoController(this)
            controller.addDefaultControlComponent(mName, false)
            mBinding.videoView.setVideoController(controller)
            mBinding.videoView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.videoView.resume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.videoView.pause()
    }

    override fun onDestroy() {
        mBinding.videoView.release()
        super.onDestroy()
    }
}