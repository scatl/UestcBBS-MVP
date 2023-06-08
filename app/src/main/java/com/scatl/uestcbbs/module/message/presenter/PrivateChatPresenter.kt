package com.scatl.uestcbbs.module.message.presenter

import android.content.DialogInterface
import android.view.View
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
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.PrivateChatView
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.uestcbbs.util.subscribeEx
import org.jsoup.Jsoup
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * Created by sca_tl at 2023/3/29 15:35
 */
class PrivateChatPresenter: BaseVBPresenter<PrivateChatView>() {

    private val messageModel = MessageModel()

    fun getPrivateMsg(hisId: Int) {
        val pmList = JSONObject().apply {
            put("body", JSONObject().apply {
                put("pmInfos", JSONArray().apply {
                    add(JSONObject().apply {
                        put("startTime", "0")
                        put("stopTime", "0")
                        put("cacheCount", "30")
                        put("pmLimit", "1000")
                        put("fromUid", hisId)
                    })
                })
            })
        }
        messageModel
            .getPrivateChatMsgList(pmList.toJSONString())
            .subscribeEx(Observer<PrivateChatBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPrivateListSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPrivateListError(it.head?.errInfo)
                    }
                }

                onError {
                    mView?.onGetPrivateListError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun sendPrivateMsg(content: String?, type: String, hisId: Int) {
        val json = JSONObject().apply {
            put("action", "send")
            put("plid", "0")
            put("pmid", "0")
            put("toUid", hisId)
            put("msg", JSONObject().apply {
                put("content", content)
                put("type", type)
            })
        }

        messageModel
            .sendPrivateMsg(json.toJSONString())
            .subscribeEx(Observer<SendPrivateMsgResultBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onSendPrivateChatMsgSuccess(it, content, type)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onSendPrivateChatMsgError(it.head?.errInfo)
                    }
                }

                onError {
                    mView?.onSendPrivateChatMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun getUserSpace(uid: Int) {
        messageModel
            .getUserSpace(uid, "profile")
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    try {
                        val document = Jsoup.parse(it)
                        val elements = document.select("div[class=bm_c u_profile]").select("div[class=pbm mbm bbda cl]")
                        val isOnline = elements[0].select("h2[class=mbn]").html().contains("在线")
                        mView?.onGetUserSpaceSuccess(isOnline)
                    } catch (e: Exception) {
                        mView?.onGetUserSpaceError(e.message)
                    }
                }

                onError {
                    mView?.onGetUserSpaceError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun deleteSinglePrivateMsg(pmid: Int, touid: Int, position: Int) {
        messageModel
            .deleteSinglePrivateMsg(pmid, touid, SharePrefUtil.getForumHash(mView?.getContext()))
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("进行的短消息操作成功")) {
                        mView?.onDeleteSinglePmSuccess("删除成功", position)
                    } else {
                        mView?.onDeleteSinglePmError("删除失败")
                    }
                }

                onError {
                    mView?.onDeleteSinglePmError("删除失败：" + it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
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
        messageModel
            .uploadImages(files, module, type)
            .subscribeEx(Observer<UploadResultBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onUploadSuccess(it)
                    } else if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onUploadError(it.head?.errInfo)
                    }
                }

                onError {
                    mView?.onUploadError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
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
            val p = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            p.setOnClickListener { v: View? ->
                dialog.dismiss()
                compressImage(files)
                mView?.getContext()?.showToast("正在发送图片，请不要重复操作！", ToastType.TYPE_NORMAL)
            }
        }
        dialog.show()
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