package com.scatl.widget.download

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.scatl.widget.R
import com.scatl.widget.databinding.ActivityDownloadBinding

/**
 * Created by sca_tl at 2023/2/28 11:06
 */
class DownloadActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityDownloadBinding
    private var mUrl: String? = ""
    private var mName: String? = ""
    private var mCookies: String? = ""
    private var mTitle: String? = "下载文件"

    private val toSAFActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            try {
                val uriTree = it.data?.data
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uriTree!!, takeFlags)
                DownLoadUtil.setDownloadFolderUri(this, uriTree.toString())
                initDownloadInfoView()
            } catch (e: Exception) {
                Toast.makeText(this, "授权失败:" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ImmersionBar.with(this).statusBarColorInt(Color.TRANSPARENT).init()

        mTitle = intent.getStringExtra("title")
        mName = intent.getStringExtra("name")
        mUrl = intent.getStringExtra("url")
        mCookies = intent.getStringExtra("cookies")

        initView()
    }

    private fun initView() {
        if (!DownLoadUtil.isDownloadFolderUriAccessible(this)) {
            initPermissionView()
        } else {
            initDownloadInfoView()
        }

        mBinding.permissionNext.setOnClickListener(this)
        mBinding.confirmButton.setOnClickListener(this)
        mBinding.contentLayout.visibility = View.VISIBLE
        mBinding.contentLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.csu_activity_appear))
    }

    private fun initPermissionView() {
        mBinding.permissionGroup.visibility = View.VISIBLE
        mBinding.infoGroup.visibility = View.GONE
        mBinding.warningText.visibility = View.GONE
    }

    private fun initDownloadInfoView() {
        mBinding.infoGroup.visibility = View.VISIBLE
        mBinding.permissionGroup.visibility = View.GONE

        mBinding.infoTitle.text = mTitle
        mBinding.fileName.text = mName
        mBinding.folderName.text = DownLoadUtil.getDownloadFolder(this)
        if (DownLoadUtil.getExistFile(this, mName) != null) {
            mBinding.confirmButton.text = "覆盖下载"
            mBinding.warningText.visibility = View.VISIBLE
        } else {
            mBinding.confirmButton.text = "确认下载"
            mBinding.warningText.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            mBinding.permissionNext -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    )
                }
                toSAFActivity.launch(intent)
            }

            mBinding.confirmButton -> {
//                SystemUtil.checkNotificationPermission(this)
                DownLoadUtil.getExistFile(this, mName)?.delete()
                val intent = Intent(this, DownloadService().javaClass).apply {
                    putExtra("url", mUrl)
                    putExtra("name", mName)
                    putExtra("cookies", mCookies)
                }
                startService(intent)
                exit()
            }
        }
    }

    private fun exit() {
        mBinding.contentLayout.visibility = View.INVISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.csu_activity_dismiss)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                finish()
                overridePendingTransition(R.anim.csu_alpha_in, R.anim.csu_alpha_out)
            }

            override fun onAnimationRepeat(animation: Animation) { }
        })
        mBinding.contentLayout.startAnimation(animation)
    }

    override fun onBackPressed() {
        exit()
    }

}