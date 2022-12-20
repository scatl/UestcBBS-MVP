package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.PrivateChatView;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 19:30
 */
public class PrivateChatPresenter extends BasePresenter<PrivateChatView> {

    private MessageModel messageModel = new MessageModel();

    public void getPrivateMsg(int hisId, Context context) {

        JSONObject pmlist = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray pmInfos = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startTime", "0");
        jsonObject.put("stopTime", "0");
        jsonObject.put("cacheCount", "30");
        jsonObject.put("pmLimit", "1000");
        jsonObject.put("fromUid", hisId);

        pmInfos.add(jsonObject);
        body.put("pmInfos", pmInfos);

        pmlist.put("body", body);

        messageModel.getPrivateChatMsgList(pmlist.toJSONString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<PrivateChatBean>() {
                    @Override
                    public void OnSuccess(PrivateChatBean privateChatBean) {
                        if (privateChatBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetPrivateListSuccess(privateChatBean);
                        }
                        if (privateChatBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPrivateListError(privateChatBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPrivateListError(e.message);
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

    public void sendPrivateMsg(String content, String type, int hisId, Context context) {

        JSONObject msg = new JSONObject();
        msg.put("content", content);
        msg.put("type", type);

        JSONObject json = new JSONObject();
        json.put("msg", msg);
        json.put("action", "send");
        json.put("plid", "0");
        json.put("pmid", "0");
        json.put("toUid", hisId);

        messageModel.sendPrivateMsg(json.toJSONString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SendPrivateMsgResultBean>() {
                    @Override
                    public void OnSuccess(SendPrivateMsgResultBean sendPrivateMsgResultBean) {
                        if (sendPrivateMsgResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSendPrivateChatMsgSuccess(sendPrivateMsgResultBean);
                        }
                        if (sendPrivateMsgResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSendPrivateChatMsgError(sendPrivateMsgResultBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSendPrivateChatMsgError(e.message);
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

    public void deleteSinglePrivateMsg(int pmid, int touid, int position) {
        messageModel.deleteSinglePrivateMsg(pmid, touid,
                SharePrefUtil.getForumHash(App.getContext()), new Observer<String>() {
                    @Override
                    public void OnSuccess(String s) {
                        if (s != null && s.contains("进行的短消息操作成功")) {
                            view.onDeleteSinglePmSuccess("删除成功", position);
                        } else {
                            view.onDeleteSinglePmError("删除失败");
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onDeleteSinglePmError("删除失败：" + e.message);
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

    /**
     * author: sca_tl
     * description: 压缩图片
     */
    public void compressImage(Context context, List<String> files) {
        List<File> successFile = new ArrayList<>();
        Luban
                .with(context)
                .load(files)
                .ignoreBy(1)
                .setTargetDir(context.getExternalFilesDir(Constant.AppPath.TEMP_PATH).getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() { }

                    @Override
                    public void onSuccess(File file) {
                        successFile.add(file);
                        if (successFile.size() == files.size()) {
                            view.onCompressImageSuccess(successFile);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onCompressImageFail(e.getMessage());
                    }
                })
                .launch();
    }

    /**
     * author: sca_tl
     * description: 上传图片
     */
    public void uploadImages(List<File> files,
                             String module,
                             String type,
                             Context context) {
        messageModel.uploadImages(context, files, module, type, new Observer<UploadResultBean>() {
            @Override
            public void OnSuccess(UploadResultBean uploadResultBean) {
                if (uploadResultBean != null) {
                    if (uploadResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                        view.onUploadSuccess(uploadResultBean);
                    } else if (uploadResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                        if (uploadResultBean.head != null) {
                            view.onUploadError(uploadResultBean.head.errInfo);
                        }
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onUploadError(e.getMessage());
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

    /**
     * author: sca_tl
     * description: 发送图片前确认
     */
    public void checkBeforeSendImage(Context context, List<String> files) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("发送图片")
                .setMessage("确定要发送这张图片吗？")
                .setPositiveButton("发送", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                dialog.dismiss();
                compressImage(context, files);
                view.showMsg("正在发送图片，请不要重复操作！");
            });
        });
        dialog.show();
    }


    /**
     * author: sca_tl
     * description: 插入表情
     */
    public void insertEmotion(Context context, EditText content, String emotion_path) {
        String emotion_name = emotion_path.substring(emotion_path.lastIndexOf("/") + 1).replace("_", ":").replace(".gif", "");
        SpannableString spannableString = new SpannableString(emotion_name);

        Bitmap bitmap = null;
        try {
            String rePath = emotion_path.replace("file:///android_asset/", "");
            InputStream is = context.getResources().getAssets().open(rePath);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        Drawable drawable = ImageUtil.bitmap2Drawable(bitmap);
        drawable.setBounds(10, 10, 80, 80);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(imageSpan, 0, emotion_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.getText().insert(content.getSelectionStart(), spannableString);
    }

    public void showDeletePrivateMsgDialog(Context context, int pmid, int touid, int position) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("删除私信")
                .setMessage("将会删除该条私信内容")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                deleteSinglePrivateMsg(pmid, touid, position);
                dialog.dismiss();
            });
        });
        dialog.show();
    }
}
