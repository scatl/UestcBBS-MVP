package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.AccountBean;
import com.scatl.uestcbbs.entity.AttachmentBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.CreateCommentView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.widget.span.CenterImageSpan;
import com.scatl.util.FilePathUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
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
                            boolean anonymous,
                            String content,
                            List<String> imgUrls,
                            List<Integer> imgIds,
                            Map<Uri, Integer> attachments,
                            Context context,
                            int currentReplyUid) {

        JSONObject json = new JSONObject();
        json.put("fid", boardId + "");
        json.put("tid", topicId + "");

        if (isQuote) {
            json.put("isQuote", "1");
            json.put("replyId", quoteId + "");
        } else {
            json.put("isQuote", "0");
        }

        json.put("isAnonymous", anonymous ? 1 : 0);

        JSONArray jsonArray = new JSONArray();

        if (!TextUtils.isEmpty(content)) { //有文字
            JSONObject content_json = new JSONObject();
            content_json.put("type", "0");
            content_json.put("infor", content);
            jsonArray.add(content_json);
        }

        boolean hasImg = (imgUrls != null && imgIds != null && imgUrls.size() != 0);
        boolean hasAttachment = (attachments != null && attachments.size() != 0);

        if (hasImg && !hasAttachment) { //有图片无附件
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
            for (int i = 0; i < imgUrls.size(); i ++) {
                JSONObject image_json = new JSONObject();
                image_json.put("type", "1");
                image_json.put("infor", imgUrls.get(i));
                jsonArray.add(image_json);
            }

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

        String token;
        String secret;
        List<AccountBean> beanList = LitePal.where("uid = " + currentReplyUid).find(AccountBean.class);
        if (beanList == null || beanList.size() == 0) {
            view.onSendCommentError("发送失败，未找到您的本地帐户信息");
            return;
        } else {
            token = beanList.get(0).token;
            secret = beanList.get(0).secret;
        }
        postModel.sendPost("reply", json_.toJSONString(), token, secret,
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
    public void uploadImg(List<File> files,
                          String module,
                          String type,
                          Context context) {
        postModel.uploadImages(context, files, module, type, new Observer<UploadResultBean>() {
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
                    view.onUploadAttachmentError("未获取到cookies或cookies失效，请重新登录");
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
                            attachmentBean.uri = uri;
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
                    || FileUtil.isPlugIn(fileName)
                    || FileUtil.isVideo(fileName)
                    || FileUtil.isPdf(fileName)) {
                if (TextUtils.isEmpty(SharePrefUtil.getUploadHash(context, SharePrefUtil.getName(context)))){
                    view.onUploadAttachmentError("需要先获取相关数据才能上传附件，请转至帐号管理页面进行授权");
                } else {
                    uploadAttachment(context, SharePrefUtil.getUid(context), fid, uri);
                    view.onStartUploadAttachment();
                }
            } else {
                view.onUploadAttachmentError("不支持的文件类型！");
            }
        }
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
        float radio = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
        Rect rect = new Rect(0, 0, (int) (content.getTextSize() * radio * 1.5f), (int) (content.getTextSize() * 1.5f));
        drawable.setBounds(rect);
        CenterImageSpan imageSpan = new CenterImageSpan(drawable);
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


}
