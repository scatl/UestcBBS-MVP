package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.MyApplication;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.MessageView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

public class MessagePresenter extends BasePresenter<MessageView> {

    private MessageModel messageModel = new MessageModel();

    public void getPrivateMsg(int page, int pageSize, Context context) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", page);
        jsonObject.put("pageSize", pageSize);

        messageModel.getPrivateMsg(jsonObject.toString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PrivateMsgBean>() {
                    @Override
                    public void OnSuccess(PrivateMsgBean privateMsgBean) {
                        if (privateMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetPrivateMsgSuccess(privateMsgBean);
                        }
                        if (privateMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPrivateMsgError(privateMsgBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPrivateMsgError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
                    }
                });
    }

    public void deletePrivateMsg(int uid, int position) {
        messageModel.deleteAllPrivateMsg(uid,
                SharePrefUtil.getForumHash(MyApplication.getContext()),
                new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s != null && s.contains("进行的短消息操作成功")) {
                    view.onDeletePrivateMsgSuccess("删除成功", position);
                } else {
                    view.onDeletePrivateMsgError("删除失败");
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeletePrivateMsgError("删除失败：" + e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void showDeletePrivateMsgDialog(Context context, String name, int uid, int position) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("删除私信")
                .setMessage("⚠️注意：该操作会同时删除您与“" + name + "”的全部私信内容，并且不可撤销。")
                .setPositiveButton("算了", null)
                .setNegativeButton("确认删除", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            p.setOnClickListener(v -> {
                deletePrivateMsg(uid, position);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
