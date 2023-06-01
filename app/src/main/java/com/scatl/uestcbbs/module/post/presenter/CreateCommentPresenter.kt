package com.scatl.uestcbbs.module.post.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.callback.OnPermission
import com.scatl.uestcbbs.entity.AccountBean
import com.scatl.uestcbbs.entity.AttachmentBean
import com.scatl.uestcbbs.entity.SendPostBean
import com.scatl.uestcbbs.entity.UploadResultBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.post.model.PostModel
import com.scatl.uestcbbs.module.post.view.CreateCommentView
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.FileUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.widget.sapn.CenterImageSpan
import com.scatl.util.ImageUtil
import com.scatl.util.FilePathUtil
import io.reactivex.disposables.Disposable
import org.litepal.LitePal.where
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.IOException

/**
 * Created by sca_tl at 2023/4/17 9:37
 */
class CreateCommentPresenter: BaseVBPresenter<CreateCommentView>() {

    private val postModel = PostModel()

    fun sendComment(boardId: Int,
                    topicId: Int,
                    quoteId: Int,
                    isQuote: Boolean,
                    anonymous: Boolean,
                    content: String?,
                    imgUrls: List<String>?,
                    imgIds: List<Int>?,
                    attachments: Map<Uri, Int>?,
                    currentReplyUid: Int) {

        val beanList = where("uid = $currentReplyUid").find(AccountBean::class.java)
        if (beanList.isNullOrEmpty()) {
            mView?.onSendCommentError("发送失败，未找到您的本地帐户信息，请删除账号重新登录")
            return
        }

        val token = beanList[0].token
        val secret = beanList[0].secret

        val json = JSONObject().apply {
            put("fid", boardId.toString())
            put("tid", topicId.toString())
            put("isAnonymous", if (anonymous) "1" else "0")
            put("isQuote", if (isQuote) "1" else "0")
            put("replyId", quoteId.toString())
        }

        val contentArray = JSONArray()

        //文本
        if (!TextUtils.isEmpty(content)) {
            contentArray.add(JSONObject().apply {
                put("type", "0")
                put("infor", content)
            })
        }

        val aid = StringBuilder()

        //图片
        if (!imgIds.isNullOrEmpty() && !imgUrls.isNullOrEmpty() && imgUrls.size == imgIds.size) {
            for ((index, url) in imgUrls.withIndex()) {
                contentArray.add(JSONObject().apply {
                    put("type", "1")
                    put("infor", url)
                })
                aid.append(imgIds[index]).append(",")
            }
        }

        //附件
        if (!attachments.isNullOrEmpty()) {
            for (entry in attachments.entries) {
                contentArray.add(JSONObject().apply {
                    put("type", "5")
                    put("infor", "[attach]" + entry.value + "[/attach]")
                })
                aid.append(entry.value).append(",")
            }
        }

        val finalJson = JSONObject().apply {
            put("body", JSONObject().apply {
                put("json", json.apply {
                    put("content", contentArray.toJSONString())
                    if (aid.isNotEmpty()) {
                        put("aid", aid.dropLast(1))
                    }
                })
            })
        }

        postModel.sendPost("reply", finalJson.toJSONString(), token, secret,
            object : Observer<SendPostBean>() {
                override fun OnSuccess(sendPostBean: SendPostBean) {
                    if (sendPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSendCommentSuccess(sendPostBean)
                    }
                    if (sendPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSendCommentError(sendPostBean.head.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onSendCommentError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun uploadImg(files: List<File>, module: String?, type: String?) {
        postModel.uploadImages(files, module, type,
            object : Observer<UploadResultBean>() {
                override fun OnSuccess(uploadResultBean: UploadResultBean) {
                    if (uploadResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onUploadSuccess(uploadResultBean)
                    } else if (uploadResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onUploadError(uploadResultBean.head?.errInfo)
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onUploadError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun compressImage(files: List<String>) {
        val successFile: MutableList<File> = ArrayList()
        Luban
            .with(mView?.getContext())
            .load(files)
            .ignoreBy(1)
            .setTargetDir(mView?.getContext()?.getExternalFilesDir(Constant.AppPath.TEMP_PATH)?.absolutePath)
            .filter { path ->
                !PictureMimeType.isGif(PictureMimeType.getImageMimeType(path))
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {

                }

                override fun onSuccess(file: File) {
                    successFile.add(file)
                    if (successFile.size == files.size) {
                        mView?.onCompressImageSuccess(successFile)
                    }
                }

                override fun onError(e: Throwable) {
                    mView?.onCompressImageFail(e.message)
                }
            })
            .launch()
    }

    fun uploadAttachment(context: Context?, uid: Int, fid: Int, uri: Uri?) {
        postModel.uploadAttachment(context, uid, fid, uri, object : Observer<String>() {
            override fun OnSuccess(s: String) {
                if (TextUtils.isEmpty(s)) {
                    mView?.onUploadAttachmentError("未获取到cookies或cookies失效，请重新登录")
                } else {
                    try {
                        val aid = s.toInt()
                        if (aid < 0) {
                            mView?.onUploadAttachmentError("上传附件失败，请重试：aid不正确，可能是参数有误，请联系开发者")
                        } else {
                            val path = FilePathUtil.getPath(context, uri)
                            val file = File(path)
                            val attachmentBean = AttachmentBean()
                            attachmentBean.aid = aid
                            attachmentBean.uri = uri
                            attachmentBean.fileName = file.name
                            attachmentBean.fileType = FileUtil.getFileType(file.name)
                            attachmentBean.localPath = file.absolutePath
                            mView?.onUploadAttachmentSuccess(attachmentBean, "上传附件成功")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mView?.onUploadAttachmentError("上传附件失败：" + e.message)
                    }
                }
            }

            override fun onError(e: ResponseThrowable) {
                e.printStackTrace()
                mView?.onUploadAttachmentError("上传附件失败：" + e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun checkBlack(tid: Int, fid: Int, quoteId: Int) {
        postModel.checkBlack(tid, fid, quoteId, object : Observer<String?>() {
            override fun OnSuccess(t: String?) {
                if (t?.contains("您在该用户的黑名单中") == true) {
                    mView?.onCheckBlack(true)
                } else {
                    mView?.onCheckBlack(false)
                }
            }

            override fun onError(e: ResponseThrowable?) {
                mView?.onCheckBlack(false)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun readyUploadAttachment(context: Context?, uri: Uri?, fid: Int) {
        val path = FilePathUtil.getPath(context, uri)
        if (!TextUtils.isEmpty(path)) {
            val file = File(path)
            val fileName = file.name
            if (FileUtil.isApplication(fileName) || FileUtil.isAudio(fileName) || FileUtil.isCompressed(fileName)
                || FileUtil.isDocument(fileName) || FileUtil.isPicture(fileName) || FileUtil.isPlugIn(fileName)
                || FileUtil.isVideo(fileName) || FileUtil.isPdf(fileName)) {

                if (TextUtils.isEmpty(SharePrefUtil.getUploadHash(context, SharePrefUtil.getName(context)))) {
                    mView?.onUploadAttachmentError("缺少参数，请重新登录或点击帐号管理页面右上角")
                } else {
                    uploadAttachment(context, SharePrefUtil.getUid(context), fid, uri)
                    mView?.onStartUploadAttachment()
                }
            } else {
                mView?.onUploadAttachmentError("不支持的文件类型！")
            }
        }
    }

    fun requestPermission(activity: FragmentActivity?, action: Int, vararg permissions: String?) {
        CommonUtil.requestPermission(activity, object : OnPermission {
            override fun onGranted() {
                mView?.onPermissionGranted(action)
            }

            override fun onRefusedWithNoMoreRequest() {
                mView?.onPermissionRefusedWithNoMoreRequest()
            }

            override fun onRefused() {
                mView?.onPermissionRefused()
            }
        }, *permissions)
    }
}