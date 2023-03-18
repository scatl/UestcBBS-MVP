package com.scatl.uestcbbs.module.message.presenter

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.alibaba.fastjson.JSONObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scatl.uestcbbs.App.Companion.getContext
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBPresenter
import com.scatl.uestcbbs.entity.PrivateMsgBean
import com.scatl.uestcbbs.helper.ExceptionHelper.ResponseThrowable
import com.scatl.uestcbbs.helper.rxhelper.Observer
import com.scatl.uestcbbs.module.message.model.MessageModel
import com.scatl.uestcbbs.module.message.view.NewMsgView
import com.scatl.uestcbbs.util.SharePrefUtil
import io.reactivex.disposables.Disposable

/**
 * Created by tanlei02 at 2023/3/15 19:40
 */
class NewMsgPresenter: BaseVBPresenter<NewMsgView>() {



}