package com.scatl.uestcbbs.module.post.model;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.annotation.UserPostType;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scatl.util.FilePathUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:32
 */
public class PostModel {
    public void getPostDetail(int page,
                              int pageSize,
                              int order,
                              int topicId,
                              int authorId,
                              String token,
                              String secret,
                              Observer<PostDetailBean> observer) {
        Observable<PostDetailBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getPostDetailList(page, pageSize, order, topicId, authorId, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void sendPost(String act,
                         String json,
                         String token,
                         String secret,
                         Observer<SendPostBean> observer) {
        Observable<SendPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .sendPost(act, json, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void favorite(String idType,
                         String action,
                         int id,
                         Observer<FavoritePostResultBean> observer) {
        Observable<FavoritePostResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .favorite(idType, action, id);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void support(int tid,
                        int pid,
                        String type,
                        String action,
                        Observer<SupportResultBean> observer) {
        Observable<SupportResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .support(tid, pid, type, action);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void userPost(int page,
                         int pageSize,
                         String type,
                         int uid,
                         Observer<CommonPostBean> observer) {
        Observable<CommonPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userPost(page, pageSize, uid, type);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void vote(int tid,
                     int boardId,
                     String options,
                     Observer<VoteResultBean> observer) {
        Observable<VoteResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .vote(tid, boardId, options);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserPost(int uid,
                            int page,
                            int pageSize,
                            String type,
                            Observer<CommonPostBean> observer) {
        Observable<CommonPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userPost(page, pageSize, uid, type);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getForumList(Observer<ForumListBean> observer) {
        Observable<ForumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .forumList();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getSubForumList(int fid, Observer<SubForumListBean> observer) {
        Observable<SubForumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .subForumList(fid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getRateInfo(int tid, int pid, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .rateInfo(tid, pid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void rate(int tid, int pid, int score, String reason, String sendreasonpm, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .rate(tid, pid, score, reason, sendreasonpm);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void report(String idType, String message, int id, Observer<ReportBean> observer) {
        Observable<ReportBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .report(idType, message, id);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void postAppendFormHash(int tid, int pid,
                                   Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .postAppendHash(tid, pid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void postAppendSubmit(int tid, int pid, String formHash, String content,
                                 Observer<String> observer) {

        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("handlekey", "");
        map.put("postappendmessage", content);
        map.put("postappendsubmit", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .postAppendSubmit(tid, pid, "yes", RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getDianPingFormHash(int tid, int pid,
                                    Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getDianPingFormHash(tid, pid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getCommentList(int tid, int pid, int page,
                               Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getCommentList(tid, pid, page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getPostWebDetail(int tid, int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getPostWebDetail(tid, page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getVoteOptions(int tid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getVoteOptions(tid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void viewVoter(int tid, int optionId, int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .viewVoter(tid, optionId, page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getRateUser(int tid, int pid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getAllRateUser(tid, pid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void sendDianPing(int tid,
                             int pid,
                             String formHash,
                             String content,
                             Observer<String> observer) {

        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("handlekey", "");
        map.put("message", content);
        map.put("commentsubmit", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .dianPingSubmit(tid, pid, RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void findPost(int ptid, int pid, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .findPost(ptid, pid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void stickReply(String formHash, int fid, int tid,
                           boolean stick, int replyId, Observer<String> observer) {

        Map<String, String> map = new HashMap<>();
        map.put("formhash", formHash);
        map.put("handlekey", "mods");
        map.put("fid", fid + "");
        map.put("tid", tid + "");
        map.put("page", "1");
        map.put("stickreply", stick ? "1" : "0");
        map.put("reason", "");
        map.put("topiclist[]", replyId + "");


        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .stickReply(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void uploadAttachment(Context context,
                                 int uid,
                                 int fid,
                                 Uri uri,
                                 Observer<String> observer) {

        String path = FilePathUtil.getPath(context, uri);
        File file = new File(path);

        Map<String, String> map = new HashMap<>();
        map.put("uid", uid + "");
        map.put("hash", SharePrefUtil.getUploadHash(context, SharePrefUtil.getName(context)));
        map.put("filetype", "");
        map.put("Filename", file.getName());

        Map<String, RequestBody> m = RetrofitCookieUtil.generateRequestBody(map);

        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024 * 10];
            int readByte;
            while ((readByte = fileInputStream.read(buff, 0, 100)) > 0) {
                outputStream.write(buff, 0, readByte);
            }
            byte[] fileByte = outputStream.toByteArray();
            parcelFileDescriptor.close();
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileByte);
            m.put("Filedata" + "\"; filename=\"" + URLEncoder.encode(file.getName(), "utf-8"), requestBody);
        } catch (Exception e) {
            ToastUtil.showToast(context, e.getMessage(), ToastType.TYPE_ERROR);
        }

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .uploadAttachment(fid, m);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void uploadImages(List<File> files,
                             String module,
                             String type,
                             Observer<UploadResultBean> observer) {
        Map<String, RequestBody> params = new HashMap<>();

        params.put("module", RequestBody.create(MediaType.parse(""), module));
        params.put("type", RequestBody.create(MediaType.parse(""), type));

        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("uploadFile[]", "ImageFile.png", requestBody);
            parts.add(part);
        }

        Observable<UploadResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .uploadImage(params, parts);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getHotPost(int page,
                           int pageSize,
                           int moduleId,
                           Observer<CommonPostBean> observer) {
        Observable<CommonPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHotPostList(page, pageSize, moduleId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getHomeTopicList(int page,
                                 int pageSize,
                                 int boardId,
                                 String sortby,
                                 Observer<CommonPostBean> observer) {
        Observable<CommonPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHomeTopicList(page, pageSize, boardId, sortby);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void sanShui(String formhash,
                        String subject,
                        String message,
                        int shuiDiCountEachReply,
                        int totaltTimes,
                        int eachOneTime,
                        int random,
                        Observer<Response<ResponseBody>> observer) {
        Map<String, String> map = new HashMap<>();

        map.put("replycredit_extcredits", String.valueOf(shuiDiCountEachReply));
        map.put("replycredit_times", String.valueOf(totaltTimes));
        map.put("replycredit_membertimes", String.valueOf(eachOneTime));
        map.put("replycredit_random", String.valueOf(random));

        map.put("formhash", formhash);
        map.put("posttime", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("wysiwyg", "1");
        map.put("typeid", "315");
        map.put("subject", subject);
        map.put("message", message);
        map.put("price", "");
        map.put("tags", "");

        map.put("cronpublishdate", "");
        map.put("allownoticeauthor", "1");
        map.put("addfeed", "1");
        map.put("usesig", "1");
        map.put("save", "");
        map.put("uploadalbum", "-2");
        map.put("newalbum", "请输入相册名称");

        Observable<Response<ResponseBody>> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .sanShui(RetrofitUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserDetail(int userId,
                              Observer<UserDetailBean> observer) {
        Observable<UserDetailBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userDetail(userId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getFormHash(Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHomeInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getWarningData(int tid,
                               int uid,
                               Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .viewWarning(tid, uid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void checkBlack(int tid,
                           int fid,
                           int quoteId,
                           Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .checkBlack(tid, fid, quoteId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
