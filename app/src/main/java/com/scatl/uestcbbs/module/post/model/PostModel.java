package com.scatl.uestcbbs.module.post.model;

import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SendPostBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.entity.VoteResultBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    public void setPost(String act,
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
                         String token,
                         String secret,
                         Observer<FavoritePostResultBean> observer) {
        Observable<FavoritePostResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .favorite(idType, action, id, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void support(int tid,
                        int pid,
                        String type,
                        String token,
                        String secret,
                        Observer<SupportResultBean> observer) {
        Observable<SupportResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .support(tid, pid, type, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void userPost(int page,
                         int pageSize,
                         String type,
                         int uid,
                         String token,
                         String secret,
                         Observer<UserPostBean> observer) {
        Observable<UserPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userPost(page, pageSize, uid, type, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void vote(int tid,
                     int boardId,
                     String options,
                     String token,
                     String secret,
                     Observer<VoteResultBean> observer) {
        Observable<VoteResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .vote(tid, boardId, options, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getHotPostList(int page,
                               int pageSize,
                               int moduleId,
                               String token,
                               String secret,
                               Observer<HotPostBean> observer) {
        Observable<HotPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHotPostList(page, pageSize, moduleId, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    //上传文件
    public void upload(List<File> files,
                       String module,
                       String type,
                       String token,
                       String secret,
                       Observer<UploadResultBean> observer){

//        MultipartBody.Builder builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM);
////                .addFormDataPart("module", module)
////                .addFormDataPart("type", type)
////                .addFormDataPart("accessToken", token)
////                .addFormDataPart("accessSecret", secret);
//
////        List<MultipartBody.Part> partList = new ArrayList<>();
//        if (files != null) {
//
//            for (int i = 0; i < files.size(); i ++) {
////                RequestBody requestBody = RequestBody.create(null, files.get(i));
////                MultipartBody.Part part = MultipartBody.Part.createFormData("uploadFile[]", files.get(i).getName(), requestBody);
////                partList.add(part);
//
//                builder.addFormDataPart("uploadFile[]", files.get(i).getName(),
//                        RequestBody.create(MediaType.parse("image/*"), files.get(i)));
//
//            }
//        }
////
////        RequestBody requestBody = builder.build();
//
////        Map<String, RequestBody> map = new HashMap<>();
////
////        if (files != null) {
////            for (int i = 0; i < files.size(); i ++) {
////                map.put("uploadFile[]; name=" + files.get(i).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(i)));
////            }
////        }
//
////        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), files.get(0));
////        MultipartBody.Part part = MultipartBody.Part.createFormData("uploadFile[]", files.get(0).getName(), requestBody);
//        Observable<UploadResultBean> observable = RetrofitUtil
//                .getInstance()
//                .getApiService()
//                .uploadImage(module, type, token, secret, builder.build());
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
    }


    public void getForumList(String token,
                             String secret,
                             Observer<ForumListBean> observer) {
        Observable<ForumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .forumList(token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getSubForumList(
                            int fid,
                            String token,
                            String secret,
                            Observer<SubForumListBean> observer) {
        Observable<SubForumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .subForumList(fid, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getSingleBoardPostList(int page,
                                       int pageSize,
                                       int topOrder,
                                       int boardId,
                                       int filterId,
                                       String filterType,
                                       String sortby,
                                       String token,
                                       String secret,
                                       Observer<SingleBoardBean> observer) {
        Observable<SingleBoardBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getSingleBoardPostList(page, pageSize, topOrder, boardId, filterId, filterType, sortby, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
