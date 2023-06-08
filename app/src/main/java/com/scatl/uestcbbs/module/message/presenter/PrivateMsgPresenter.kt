package com.scatl.uestcbbs.module.message.presenter

import com.alibaba.fastjson.JSONObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.PrivateMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.subscribeEx

/**
 * Created by sca_tl at 2023/3/16 11:32
 */
class PrivateMsgPresenter: BaseVBPresenter<PrivateMsgView>() {

    private val messageModel = MessageModel()

    fun getPrivateMsg(page: Int, pageSize: Int) {
        val jsonObject = JSONObject().apply {
            put("page", page)
            put("pageSize", pageSize)
        }

        messageModel
            .getPrivateMsg(jsonObject.toString())
            .subscribeEx(Observer<PrivateMsgBean>().observer {
                onSuccess {
                    if (it.rs == ApiConstant.Code.SUCCESS_CODE) {
                        mView?.onGetPrivateMsgSuccess(it)
                    }
                    if (it.rs == ApiConstant.Code.ERROR_CODE) {
                        mView?.onGetPrivateMsgError(it.head.errInfo)
                    }
                }

                onError {
                    mView?.onGetPrivateMsgError(it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
                }
            })
    }

    fun deletePrivateMsg(uid: Int, position: Int) {
        messageModel
            .deleteAllPrivateMsg(uid, SharePrefUtil.getForumHash(mView?.getContext()))
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("进行的短消息操作成功")) {
                        mView?.onDeletePrivateMsgSuccess("删除成功", position)
                    } else {
                        mView?.onDeletePrivateMsgError("删除失败")
                    }
                }

                onError {
                    mView?.onDeletePrivateMsgError("删除失败：" + it.message)
                }

                onSubscribe {
                    mCompositeDisposable?.add(it)
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