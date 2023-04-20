package com.scatl.uestcbbs.module.message.presenter

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.PrivateChatBean
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean
import com.scatl.uestcbbs.entity.UploadResultBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.PrivateChatView
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.ImageUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.uestcbbs.widget.span.CenterImageSpan
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/3/29 15:35
 */
class PrivateChatPresenter: BaseVBPresenter<PrivateChatView>() {

    private val messageModel = MessageModel()

    fun getPrivateMsg(hisId: Int) {
        val pmlist = JSONObject()
        val body = JSONObject()
        val pmInfos = JSONArray()
        val jsonObject = JSONObject()

        jsonObject["startTime"] = "0"
        jsonObject["stopTime"] = "0"
        jsonObject["cacheCount"] = "30"
        jsonObject["pmLimit"] = "1000"
        jsonObject["fromUid"] = hisId

        pmInfos.add(jsonObject)
        body["pmInfos"] = pmInfos
        pmlist["body"] = body

        messageModel.getPrivateChatMsgList(pmlist.toJSONString(), object : Observer<PrivateChatBean>() {
            override fun OnSuccess(privateChatBean: PrivateChatBean) {
                if (privateChatBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    mView?.onGetPrivateListSuccess(privateChatBean)
                } else if (privateChatBean.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onGetPrivateListError(privateChatBean.head.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetPrivateListError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun sendPrivateMsg(content: String?, type: String, hisId: Int) {
        val msg = JSONObject()
        msg["content"] = content
        msg["type"] = type
        val json = JSONObject()
        json["msg"] = msg
        json["action"] = "send"
        json["plid"] = "0"
        json["pmid"] = "0"
        json["toUid"] = hisId
        messageModel.sendPrivateMsg(json.toJSONString(), object : Observer<SendPrivateMsgResultBean>() {
            override fun OnSuccess(sendPrivateMsgResultBean: SendPrivateMsgResultBean) {
                if (sendPrivateMsgResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    mView?.onSendPrivateChatMsgSuccess(sendPrivateMsgResultBean, content, type)
                } else if (sendPrivateMsgResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                    mView?.onSendPrivateChatMsgError(sendPrivateMsgResultBean.head.errInfo)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onSendPrivateChatMsgError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun getUserSpace(uid: Int) {
        messageModel.getUserSpace(uid, "profile", object : Observer<String>() {
            override fun OnSuccess(s: String) {
                try {
                    val document = Jsoup.parse(s)
                    val elements = document.select("div[class=bm_c u_profile]").select("div[class=pbm mbm bbda cl]")
                    val isOnline = elements[0].select("h2[class=mbn]").html().contains("在线")
                    mView?.onGetUserSpaceSuccess(isOnline,)
                } catch (e: Exception) {
                    mView?.onGetUserSpaceError(e.message)
                }
            }

            override fun onError(e: ResponseThrowable) {
                mView?.onGetUserSpaceError(e.message)
            }

            override fun OnCompleted() {

            }

            override fun OnDisposable(d: Disposable) {
                mCompositeDisposable?.add(d)
            }
        })
    }

    fun deleteSinglePrivateMsg(pmid: Int, touid: Int, position: Int) {
        messageModel.deleteSinglePrivateMsg(pmid, touid,
            SharePrefUtil.getForumHash(mView?.getContext()), object : Observer<String?>() {
                override fun OnSuccess(s: String?) {
                    if (s != null && s.contains("进行的短消息操作成功")) {
                        mView?.onDeleteSinglePmSuccess("删除成功", position)
                    } else {
                        mView?.onDeleteSinglePmError("删除失败")
                    }
                }

                override fun onError(e: ResponseThrowable) {
                    mView?.onDeleteSinglePmError("删除失败：" + e.message)
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
            .filter { path ->
                !PictureMimeType.isGif(PictureMimeType.getImageMimeType(path))
            }
            .setTargetDir(mView?.getContext()?.getExternalFilesDir(Constant.AppPath.TEMP_PATH)?.absolutePath)
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


    fun uploadImages(files: List<File>, module: String, type: String) {
        messageModel.uploadImages(mView?.getContext(), files, module, type,
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

    fun checkBeforeSendImage(files: List<String>) {
        val dialog = MaterialAlertDialogBuilder(mView?.getContext()!!)
            .setTitle("发送图片")
            .setMessage("确定要发送这张图片吗？")
            .setPositiveButton("发送", null)
            .setNegativeButton("取消", null)
            .create()
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val p =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            p.setOnClickListener { v: View? ->
                dialog.dismiss()
                compressImage(files)
                mView?.getContext()?.showToast("正在发送图片，请不要重复操作！", ToastType.TYPE_NORMAL)
            }
        }
        dialog.show()
    }


    fun insertEmotion(context: Context, content: EditText, emotion_path: String) {
        val emotion_name = emotion_path.substring(emotion_path.lastIndexOf("/") + 1)
            .replace("_", ":")
            .replace(".gif", "")
        val spannableString = SpannableString(emotion_name)
        var bitmap: Bitmap? = null
        try {
            val rePath = emotion_path.replace("file:///android_asset/", "")
            val `is` = context.resources.assets.open(rePath)
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val drawable = ImageUtil.bitmap2Drawable(bitmap)
        val radio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        val rect = Rect(0, 0, (content.textSize * radio * 1.5f).toInt(), (content.textSize * 1.5f).toInt())
        drawable.bounds = rect
        val imageSpan = CenterImageSpan(drawable)
        spannableString.setSpan(imageSpan, 0, emotion_name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        content.text.insert(content.selectionStart, spannableString)
    }

    fun showDeletePrivateMsgDialog(pmid: Int, touid: Int, position: Int) {
        val dialog = MaterialAlertDialogBuilder(mView?.getContext()!!)
            .setTitle("删除私信")
            .setMessage("将会删除该条私信内容")
            .setPositiveButton("确认", null)
            .setNegativeButton("取消", null)
            .create()
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val p = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            p.setOnClickListener { v: View? ->
                deleteSinglePrivateMsg(pmid, touid, position)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}