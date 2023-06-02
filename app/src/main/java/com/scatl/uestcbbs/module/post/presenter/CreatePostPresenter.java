package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.luck.picture.lib.config.PictureMimeType;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.util.BBSLinkUtil;
import com.scatl.uestcbbs.widget.ContentEditor;
import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.CreatePostView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.util.FilePathUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;
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
            return;
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
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !PictureMimeType.isGif(PictureMimeType.getImageMimeType(path));
                    }
                })
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
        postModel.uploadImages(files, module, type, new Observer<UploadResultBean>() {
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

    public void uploadAttachment(Context context, int uid, int fid, Uri uri){
        postModel.uploadAttachment(context, uid, fid, uri, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (TextUtils.isEmpty(s)) {
                    view.onUploadAttachmentError("请获取Cookies后使用上传附件功能");
                } else {
                    try {
                        int aid = Integer.parseInt(s);

                        if (aid < 0) {
                            view.onUploadAttachmentError("上传附件失败，请重试：aid不正确，可能是参数有误，请联系开发者");
                        } else {
                            String path = FilePathUtil.getPath(context, uri);
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
        String path = FilePathUtil.getPath(context, uri);
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

    public void userPost(int uid) {
        postModel.getUserPost(uid, 1, 5, "topic",
                new Observer<CommonPostBean>() {
                    @Override
                    public void OnSuccess(CommonPostBean userPostBean) {
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

    public void sanShui(Context context,
                        String subject,
                        String message,
                        int shuiDiCountEachReply,
                        int totaltTimes,
                        int eachOneTime,
                        int random) {
        postModel.sanShui(SharePrefUtil.getForumHash(context),
                subject, message, shuiDiCountEachReply, totaltTimes, eachOneTime, random,
                new Observer<Response<ResponseBody>>() {
                    @Override
                    public void OnSuccess(Response<ResponseBody> response) {
                        try {
                            if (response.body().string().contains("尚未登录")) {
                                view.onSanShuiError("散水失败，cookies无效，请重新登录");
                            } else {
                                String postUrl = response.raw().request().url().toString();
                                if (postUrl.contains("mod=viewthread")) {
                                    int id = BBSLinkUtil.getLinkInfo(postUrl).getId();
                                    if (id > 0) {
                                        view.onSanShuiSuccess(id);
                                    } else {
                                        view.onSanShuiError("不确定是否散水成功，请自行检查！");
                                    }
                                } else {
                                    view.onSanShuiError("散水失败！可能是formhash不正确，请重新登录");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            view.onSanShuiError("不确定是否散水成功，请自行检查！" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onSanShuiError(e.message);
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

    public void getUserDetail(int uid) {
        postModel.getUserDetail(uid, new Observer<UserDetailBean>() {
            @Override
            public void OnSuccess(UserDetailBean userDetailBean) {
                if (userDetailBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetUserDetailSuccess(userDetailBean);
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

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

    public void getFormHash(Context context) {
        postModel.getFormHash(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {
                    Document document = Jsoup.parse(s);
                    String formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                    if (!TextUtils.isEmpty(formHash)) {
                        SharePrefUtil.setForumHash(context, formHash);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }
}
