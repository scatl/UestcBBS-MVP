package com.scatl.uestcbbs.module.message.presenter

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AlertDialog
import com.alibaba.fastjson.JSONObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.PrivateMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by tanlei02 at 2023/3/16 11:32
 */
class PrivateMsgPresenter: BaseVBPresenter<PrivateMsgView>() {

    private val messageModel = MessageModel()

    fun getPrivateMsg(page: Int, pageSize: Int) {
        val jsonObject = JSONObject().apply {
            put("page", page)
            put("pageSize", pageSize)
        }

        messageModel.getPrivateMsg(jsonObject.toString(),
            SharePrefUtil.getToken(mView?.getContext()),
            SharePrefUtil.getSecret(mView?.getContext()),
            object : Observer<PrivateMsgBean>() {
                override fun OnSuccess(privateMsgBean: PrivateMsgBean) {
                    if (privateMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPrivateMsgSuccess(privateMsgBean)
                    }
                    if (privateMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPrivateMsgError(privateMsgBean.head.errInfo)
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable) {
                    mView?.onGetPrivateMsgError(e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun deletePrivateMsg(uid: Int, position: Int) {
        messageModel.deleteAllPrivateMsg(uid, SharePrefUtil.getForumHash(mView?.getContext()),
            object : Observer<String?>() {
                override fun OnSuccess(s: String?) {
                    if (s != null && s.contains("进行的短消息操作成功")) {
                        mView?.onDeletePrivateMsgSuccess("删除成功", position)
                    } else {
                        mView?.onDeletePrivateMsgError("删除失败")
                    }
                }

                override fun onError(e: ExceptionHelper.ResponseThrowable) {
                    mView?.onDeletePrivateMsgError("删除失败：" + e.message)
                }

                override fun OnCompleted() {

                }

                override fun OnDisposable(d: Disposable) {
                    mCompositeDisposable?.add(d)
                }
            })
    }

    fun showDeletePrivateMsgDialog(name: String, uid: Int, position: Int) {
        mView?.getContext()?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("删除私信")
                .setMessage("⚠️注意：该操作会同时删除您与“$name”的全部私信内容，并且不可撤销。")
                .setPositiveButton("算了", null)
                .setNegativeButton("确认删除") { dialog, which ->
                    deletePrivateMsg(uid, position)
                    dialog.dismiss()
                }
                .create()
        }?.show()
    }

}