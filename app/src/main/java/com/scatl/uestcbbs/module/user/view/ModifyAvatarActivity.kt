package com.scatl.uestcbbs.module.user.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Process
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.R
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityModifyAvatarBinding
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector
import com.scatl.uestcbbs.module.main.view.MainActivity
import com.scatl.uestcbbs.module.user.presenter.ModifyAvatarPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.FileUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.FileUtil.fileToBase64
import com.scatl.util.ImageUtil.bitmapToBase64

/**
 * Created by sca_tl at 2023/6/14 17:09
 */
class ModifyAvatarActivity: BaseVBActivity<ModifyAvatarPresenter, ModifyAvatarView, ActivityModifyAvatarBinding>(), ModifyAvatarView {

    private var mAgent: String? = ""
    private var mInput: String? = ""

    private var avatar1Base64: String? = ""
    private var avatar2Base64: String? = ""
    private var avatar3Base64: String? = ""

    private var avatarAllSelected = false
    private var avatar1Selected = false
    private var avatar2Selected = false
    private var avatar3Selected = false

    companion object {
        const val STATIC_PIC_SELECT = 1
        const val AVATAR1_PIC_SELECT = 2
        const val AVATAR2_PIC_SELECT = 3
        const val AVATAR3_PIC_SELECT = 4
    }

    override fun getViewBinding() = ActivityModifyAvatarBinding.inflate(layoutInflater)

    override fun initPresenter() = ModifyAvatarPresenter()

    override fun getContext() = this

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        bindClickEvent(mBinding.avatar200, mBinding.avatar120, mBinding.avatar48, mBinding.selectPicBtn, mBinding.uploadPicBtn, mBinding.restartBtn)
        mBinding.statusView.loading(mBinding.nestedLayout, mBinding.restartBtn)
        mPresenter?.getParams()
    }

    override fun onClick(v: View) {
        when(v) {
            mBinding.selectPicBtn -> {
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(false)
                    .showCropFrame(true)
                    .hideBottomControls(false)
                    .theme(R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .isEnableCrop(true)
                    .cropImageWideHigh(200, 200)
                    .withAspectRatio(1, 1)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(STATIC_PIC_SELECT)
            }
            mBinding.avatar200 -> {
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(true)
                    .theme(R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(AVATAR1_PIC_SELECT)
            }
            mBinding.avatar120 -> {
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(true)
                    .theme(R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(AVATAR2_PIC_SELECT)
            }
            mBinding.avatar48 -> {
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .isGif(true)
                    .theme(R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(AVATAR3_PIC_SELECT)
            }
            mBinding.restartBtn -> {
                FileUtil.deleteDir(cacheDir, false)
                FileUtil.deleteDir(getExternalFilesDir(Constant.AppPath.TEMP_PATH), false)
                val killIntent = Intent(this, MainActivity::class.java)
                killIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(killIntent)
                Process.killProcess(Process.myPid())
                finish()
                System.exit(1)
            }
            mBinding.uploadPicBtn -> {
                if (avatarAllSelected) {
                    mBinding.uploadPicBtn.isEnabled = false
                    mBinding.uploadPicBtn.text = "请稍候..."
                    mPresenter?.modifyAvatar(mAgent, mInput, avatar1Base64, avatar2Base64, avatar3Base64)
                } else {
                    showToast("请选择头像", ToastType.TYPE_WARNING)
                }
            }
        }
    }

    override fun onGetParamsSuccess(agent: String?, input: String?) {
        mBinding.statusView.success()
        mAgent = agent
        mInput = input
        mBinding.avatar200.load(SharePrefUtil.getAvatar(this))
        mBinding.avatar120.load(SharePrefUtil.getAvatar(this))
        mBinding.avatar48.load(SharePrefUtil.getAvatar(this))
    }

    override fun onGetParamsError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    override fun onUploadSuccess(msg: String?) {
        mBinding.uploadPicBtn.isEnabled = true
        mBinding.uploadPicBtn.text = "确认更改"
        showToast(msg, ToastType.TYPE_SUCCESS)
    }

    override fun onUploadError(msg: String?) {
        mBinding.uploadPicBtn.isEnabled = true
        mBinding.uploadPicBtn.text = "确认更改"
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == STATIC_PIC_SELECT) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList.size != 0 && selectList[0].isCut) {
                try {
                    val bitmap200 = BitmapFactory.decodeFile(selectList[0].cutPath)
                    val originalW = bitmap200.width
                    val originalH = bitmap200.height
                    if (originalW < 200 || originalH < 200) {
                        showToast("抱歉，图片尺寸不合法，请选择大于200X200的图片", ToastType.TYPE_ERROR)
                    } else {
                        mBinding.avatar200.setImageBitmap(bitmap200)

                        val matrix = Matrix()
                        matrix.setScale(0.6f, 0.6f)
                        val bitmap120 = Bitmap.createBitmap(bitmap200, 0, 0, originalW, originalH, matrix, true)
                        mBinding.avatar120.setImageBitmap(bitmap120)

                        matrix.setScale(0.24f, 0.24f)
                        val bitmap48 = Bitmap.createBitmap(bitmap200, 0, 0, originalW, originalH, matrix, true)
                        mBinding.avatar48.setImageBitmap(bitmap48)

                        avatar1Base64 = bitmapToBase64(bitmap200)
                        avatar2Base64 = bitmapToBase64(bitmap120)
                        avatar3Base64 = bitmapToBase64(bitmap48)

                        avatarAllSelected = true
                    }
                } catch (e: Exception) {
                    showToast("抱歉，出现了一个错误：" + e.message, ToastType.TYPE_ERROR)
                }
            }
        }
        if (resultCode == RESULT_OK) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList.size != 0) {
                try {
                    val gif = selectList[0]
                    val avatarUri = gif.realPath
                    when (requestCode) {
                        AVATAR1_PIC_SELECT -> {
                            if (gif.width <= 200 && gif.height <= 200) {
                                mBinding.avatar200.load(avatarUri)
                                avatar1Base64 = fileToBase64(avatarUri)
                                avatar1Selected = true
                            } else {
                                showToast("请选择图片尺寸小于200*200的图片", ToastType.TYPE_WARNING)
                            }
                        }

                        AVATAR2_PIC_SELECT -> {
                            if (gif.width <= 120 && gif.height <= 120) {
                                mBinding.avatar120.load(avatarUri)
                                avatar2Base64 = fileToBase64(avatarUri)
                                avatar2Selected = true
                            } else {
                                showToast("请选择图片尺寸小于120*120的图片", ToastType.TYPE_WARNING)
                            }
                        }

                        AVATAR3_PIC_SELECT -> {
                            if (gif.width <= 48 && gif.height <= 48) {
                                mBinding.avatar48.load(avatarUri)
                                avatar3Base64 = fileToBase64(avatarUri)
                                avatar3Selected = true
                            } else {
                                showToast("请选择图片尺寸小于48*48的图片", ToastType.TYPE_WARNING)
                            }
                        }
                    }

                    avatarAllSelected = avatar1Selected && avatar2Selected && avatar3Selected
                } catch (e: Exception) {
                    showToast("抱歉，出现了一个错误：" + e.message, ToastType.TYPE_ERROR)
                }
            }
        }
    }
}