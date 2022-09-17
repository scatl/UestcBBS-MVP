package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.custom.posteditor.ContentEditor;
import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.CreatePostView;
import com.scatl.uestcbbs.module.user.view.LocalBlackListFragment;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.FileUtils;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/9 13:52
 */
public class CreatePostPresenter extends BasePresenter<CreatePostView> {

    PostModel postModel = new PostModel();

    public void sendPost(ContentEditor contentEditor,
                         int boardId,
                         int filterId,
                         String title,
                         List<String> imgUrls,
                         List<Integer> imgIds,
                         List<String> pollOptions,
                         Map<Uri, Integer> attachments,
                         int pollChoices,
                         int pollExp,
                         boolean pollVisible,
                         boolean pollShowVoters,
                         boolean anonymous,
                         boolean onlyAuthor,
                         Context context) {

        JSONObject json = new JSONObject();

        if (pollOptions != null && pollOptions.size() != 0) {
            JSONObject pollInfo = new JSONObject();
            pollInfo.put("expiration", pollExp);
            pollInfo.put("maxChoices", pollChoices);
            pollInfo.put("visibleAfterVote", pollVisible);
            pollInfo.put("showVoters", pollShowVoters);

            JSONArray op = new JSONArray();
            op.addAll(pollOptions);
            pollInfo.put("options", op);

            json.put("poll", pollInfo);
        }

        json.put("fid", boardId);
        json.put("typeId", filterId);
        json.put("isAnonymous", anonymous ? 1 : 0);
        json.put("isOnlyAuthor", onlyAuthor ? 1 : 0);
        json.put("title", title);
        json.put("isQuote", 0);

        JSONArray jsonArray = new JSONArray();
        int img_index = 0;
        try {
            List<ContentEditor.EditData> data =  contentEditor.buildEditorData();
            for (int i = 0; i < data.size(); i ++) {
                JSONObject jsonObject = new JSONObject();
                if (data.get(i).content_type == ContentEditor.CONTENT_TYPE_TEXT) {
                    jsonObject.put("type", 0);
                    jsonObject.put("infor", data.get(i).inputStr);
                    jsonArray.add(jsonObject);
                }

                if (data.get(i).content_type == ContentEditor.CONTENT_TYPE_IMAGE) {
                    jsonObject.put("type", 1);
                    jsonObject.put("infor", imgUrls.get(img_index));
                    jsonArray.add(jsonObject);
                    img_index = img_index + 1;
                }
            }
        } catch (Exception e) {
            view.onSendPostError("发表帖子失败：" + e.getMessage());
        }

        //////
        boolean hasImg = (imgUrls != null && imgIds != null && imgUrls.size() != 0);
        boolean hasAttachment = (attachments != null && attachments.size() != 0);
        if (hasImg && !hasAttachment) { //有图片无附件
            json.put("aid", imgIds.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));

        } else if (! hasImg && hasAttachment) { //有附件无图片

            StringBuilder attaAid = new StringBuilder();

            for (Map.Entry<Uri, Integer> m : attachments.entrySet()) {
                attaAid.append(m.getValue()).append(",");

                JSONObject qq = new JSONObject();
                qq.put("type", "5");
                qq.put("infor", "[attach]" + m.getValue() + "[/attach]");
                jsonArray.add(qq);
            }

            String aa = attaAid.toString();

            json.put("aid", aa.replace(aa.charAt(aa.length() - 1) + "", ""));

        } else if (hasImg && hasAttachment) { //有附件有图片

            ///////
            String aa = imgIds.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", "");
            //////
            StringBuilder attaAid = new StringBuilder();
            for (Map.Entry<Uri, Integer> m : attachments.entrySet()) {
                attaAid.append(m.getValue()).append(",");

                JSONObject qq = new JSONObject();
                qq.put("type", "5");
                qq.put("infor", "[attach]" + m.getValue() + "[/attach]");
                jsonArray.add(qq);
            }

            String bb = attaAid.toString();
            json.put("aid", aa + "," + bb.replace(bb.charAt(bb.length() - 1) + "", ""));

        }


        json.put("content", jsonArray.toJSONString());

        JSONObject body = new JSONObject();
        body.put("json", json);

        JSONObject json_ = new JSONObject();
        json_.put("body", body);

        postModel.sendPost("new",
                json_.toJSONString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SendPostBean>() {
                    @Override
                    public void OnSuccess(SendPostBean sendPostBean) {
                        if (sendPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onSendPostSuccess(sendPostBean);
                        }
                        if (sendPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onSendPostError(sendPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSendPostError(e.message);
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
                .url(ApiConstant.BBS_BASE_URL + ApiConstant.Message.UPLOAD_IMG)
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

    public void uploadAttachment(Context context, int uid, int fid, Uri uri){
        postModel.uploadAttachment(context, uid, fid, uri, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (TextUtils.isEmpty(s)) {
                    view.onUploadAttachmentError("请重新授权后使用上传附件功能：我的->帐号管理->高级授权");
                } else {
                    try {
                        int aid = Integer.parseInt(s);

                        if (aid < 0) {
                            view.onUploadAttachmentError("上传附件失败，请重试：aid不正确，可能是参数有误，请联系开发者");
                        } else {
                            String path = FileUtils.getPath(context, uri);
                            File file = new File(path);
                            AttachmentBean attachmentBean = new AttachmentBean();
                            attachmentBean.aid = aid;
                            attachmentBean.fileName = file.getName();
                            attachmentBean.fileType = FileUtil.getFileType(file.getName());
                            attachmentBean.localPath = file.getAbsolutePath();
                            view.onUploadAttachmentSuccess(attachmentBean, "上传附件成功");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        view.onUploadAttachmentError("上传附件失败：" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                e.printStackTrace();
                view.onUploadAttachmentError("上传附件失败：" + e.message);
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

    public void readyUploadAttachment(Context context, Uri uri, int fid) {
        String path = FileUtils.getPath(context, uri);
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            String fileName =  file.getName();

            if (FileUtil.isApplication(fileName)
                    || FileUtil.isAudio(fileName)
                    || FileUtil.isCompressed(fileName)
                    || FileUtil.isDocument(fileName)
                    || FileUtil.isPicture(fileName)
                    || FileUtil.isPdf(fileName)
                    || FileUtil.isPlugIn(fileName)
                    || FileUtil.isVideo(fileName)) {
                if (TextUtils.isEmpty(SharePrefUtil.getUploadHash(context, SharePrefUtil.getName(context)))){
                    view.onUploadAttachmentError("需要先获取相关数据才能上传附件，请转至帐号管理页面，点击右上角，获取参数");
                } else {
                    uploadAttachment(context, SharePrefUtil.getUid(context), fid, uri);
                    view.onStartUploadAttachment();
                }
            } else {
                view.onUploadAttachmentError("不支持的文件类型！");
            }
        }
    }

    public void userPost(int uid,
                         Context context) {
        postModel.getUserPost( uid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<UserPostBean>() {
                    @Override
                    public void OnSuccess(UserPostBean userPostBean) {
                        if (userPostBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetUserPostSuccess(userPostBean);
                        }

                        if (userPostBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetUserPostError(userPostBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetUserPostError(e.message);
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
     * description: 发帖成功对话框
     */
    public void showCreatePostSuccessDialog(Context context) {
        final View success_view = LayoutInflater.from(context).inflate(R.layout.dialog_create_post_success, new LinearLayout(context));

        final AlertDialog success_dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("查看帖子", null)
                .setNegativeButton("返回", null)
                .setView(success_view)
                .setCancelable(false)
                .create();
        success_dialog.setOnShowListener(dialogInterface -> {
            Button p = success_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button n = success_dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            p.setOnClickListener(v -> {
                view.onSendPostSuccessViewPost();
                success_dialog.dismiss();
            });

            n.setOnClickListener(v -> {
                view.onSendPostSuccessBack();
                success_dialog.dismiss();
            });

        });
        success_dialog.show();
    }

    public void showCreatePostMoreOptionsDialog(Context context, boolean isAnonymous, boolean isOnlyAuthor, boolean originalPic) {
        final View more_options_view = LayoutInflater.from(context).inflate(R.layout.dialog_create_post_more_options, new LinearLayout(context));
        CheckBox anonymousBox = more_options_view.findViewById(R.id.create_post_more_options_anonymous);
        CheckBox onlyAuthorBox = more_options_view.findViewById(R.id.create_post_more_options_only_user);
        CheckBox originalPicBox = more_options_view.findViewById(R.id.create_post_more_options_original_pic);
        originalPicBox.setChecked(originalPic);
        anonymousBox.setChecked(isAnonymous);
        onlyAuthorBox.setChecked(isOnlyAuthor);

        final AlertDialog more_options_dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("更多选项")
                .setPositiveButton("确认", null)
                .setView(more_options_view)
                .setCancelable(false)
                .create();
        more_options_dialog.setOnShowListener(dialogInterface -> {
            Button p = more_options_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            p.setOnClickListener(v -> {
                view.onMoreOptionsChanged(anonymousBox.isChecked(), onlyAuthorBox.isChecked(), originalPicBox.isChecked());
                more_options_dialog.dismiss();
            });

        });
        more_options_dialog.show();
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

}
