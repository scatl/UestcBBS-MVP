package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.AtMeMsgActivity;
import com.scatl.uestcbbs.module.message.view.PrivateChatView;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
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
                        SubscriptionManager.getInstance().add(d);
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
                        SubscriptionManager.getInstance().add(d);
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
     * FIXME 使用retrofit上传图片返回的结果没有图片信息。先使用这种方法，有时间再研究一下
     */
    public void upload(List<File> files,
                       String module,
                       String type,
                       Context context) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(20000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        Map<String, String> map = new HashMap<>();
        map.put("module", module);
        map.put("type", type);
        map.put("accessToken", SharePrefUtil.getToken(context));
        map.put("accessSecret", SharePrefUtil.getSecret(context));

        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i ++) {
            postFormBuilder.addFile("uploadFile[]", files.get(i).getName(), files.get(i));
        }

        postFormBuilder
                .url(ApiConstant.BBS_BASE_URL + ApiConstant.SendMessage.UPLOAD_IMG)
                .params(map)
                .addHeader("content-type","multipart/form-data")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        view.onUploadError(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (JSONObject.isValidObject(response)) {
                            UploadResultBean uploadResultBean = JSON.toJavaObject(JSONObject.parseObject(response), UploadResultBean.class);
                            if (uploadResultBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                                view.onUploadSuccess(uploadResultBean);
                            }

                            if (uploadResultBean.rs == ApiConstant.Code.ERROR_CODE) {
                                view.onUploadError(uploadResultBean.head.errInfo);
                            }
                        }
                    }
                });

    }


    /**
     * author: sca_tl
     * description: 请求权限
     */
    public void requestPermission(FragmentActivity activity, final int action, String... permissions) {
        CommonUtil.requestPermission(activity, new OnPermission() {
            @Override
            public void onGranted() {
                view.onPermissionGranted(action);
            }

            @Override
            public void onRefusedWithNoMoreRequest() {
                view.onPermissionRefusedWithNoMoreRequest();
            }

            @Override
            public void onRefused() {
                view.onPermissionRefused();
            }
        }, permissions);
    }


    /**
     * author: sca_tl
     * description: 发送图片前确认
     */
    public void checkBeforeSendImage(Context context, List<String> files) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
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
}
