package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.CreateCommentView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

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
 * date: 2020/1/25 13:08
 */
public class CreateCommentPresenter extends BasePresenter<CreateCommentView> {

    private PostModel postModel = new PostModel();

    public void sendComment(int boardId,
                            int topicId,
                            int quoteId,
                            boolean isQuote,
                            String content,
                            List<String> imgUrls,
                            List<Integer> imgIds,
                            Context context) {

        JSONObject json = new JSONObject();
        json.put("fid", boardId + "");
        json.put("tid", topicId + "");

        if (isQuote) {
            json.put("isQuote", "1");
            json.put("replyId", quoteId + "");
        } else {
            json.put("isQuote", "0");
        }

        JSONArray jsonArray = new JSONArray();

        if (!TextUtils.isEmpty(content)) {
            JSONObject content_json = new JSONObject();
            content_json.put("type", "0");
            content_json.put("infor", content);
            jsonArray.add(content_json);
        }

        if (imgUrls != null && imgIds != null && imgUrls.size() != 0) { //有图片
            json.put("aid", imgIds.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));

            for (int i = 0; i < imgUrls.size(); i ++) {
                JSONObject image_json = new JSONObject();
                image_json.put("type", "1");
                image_json.put("infor", imgUrls.get(i));
                jsonArray.add(image_json);
            }
        }

        json.put("content", jsonArray.toJSONString());

        JSONObject body = new JSONObject();
        body.put("json", json);

        JSONObject json_ = new JSONObject();
        json_.put("body", body);

        postModel.sendPost("reply",
                json_.toJSONString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SendPostBean>() {
                    @Override
                    public void OnSuccess(SendPostBean sendPostBean) {
                        if (sendPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSendCommentSuccess(sendPostBean, imgUrls);
                        }
                        if (sendPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSendCommentError(sendPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSendCommentError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
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
     * description: 插入表情
     */
    public void insertEmotion(Context context, AppCompatEditText content, String emotion_path) {
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
     * description: 退出编辑警告
     */
    public void checkBeforeExit(Context context, boolean empty) {
        if (!empty) {
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setNegativeButton("确认退出", null)
                    .setPositiveButton("继续编辑", null )
                    .setTitle("退出编辑")
                    .setMessage("确认退出编辑吗？你将丢失已经编辑的内容")
                    .create();
            dialog.setOnShowListener(dialogInterface -> {
                Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                n.setOnClickListener(v -> {
                    dialog.dismiss();
                    view.onExit();
                });
            });
            dialog.show();
        } else {
            view.onExit();
        }
    }

}
