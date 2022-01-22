package com.scatl.uestcbbs.module.message.model;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.entity.DianPingMessageBean;
import com.scatl.uestcbbs.entity.PrivateChatBean;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.entity.UploadResultBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.ForumUtil;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:45
 */
public class MessageModel {
    public void getSystemMsg( int page,
                              int pageSize,
                              String token,
                              String secret,
                              Observer<SystemMsgBean> observer) {
        Observable<SystemMsgBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .systemMsg(page, pageSize, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getAtMeMsg(int page,
                           int pageSize,
                           String token,
                           String secret,
                           Observer<AtMsgBean> observer) {
        Observable<AtMsgBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .atMsg(page, pageSize, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getReplyMsg(int page,
                              int pageSize,
                              String token,
                              String secret,
                              Observer<ReplyMeMsgBean> observer) {
        Observable<ReplyMeMsgBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .replyMeMsg(page, pageSize, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getPrivateMsg(String json,
                              String token,
                              String secret,
                              Observer<PrivateMsgBean> observer) {
        Observable<PrivateMsgBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .privateMsg(ForumUtil.getAppHashValue(), json, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getPrivateChatMsgList(String json,
                              String token,
                              String secret,
                              Observer<PrivateChatBean> observer) {
        Observable<PrivateChatBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .privateChatMsgList(json, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void sendPrivateMsg(String json,
                              String token,
                              String secret,
                              Observer<SendPrivateMsgResultBean> observer) {
        Observable<SendPrivateMsgResultBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .sendPrivateMsg(json, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getDianPingMsg(int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getDianPingMsg1(page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteAllPrivateMsg(int uid, String formHash, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("deletepm_deluid[]", uid + "");
        map.put("custompage", "1");
        map.put("deletepmsubmit_btn", "true");
        map.put("deletesubmit", "true");
        map.put("formhash", formHash);

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .deletePrivateMsg(RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteSinglePrivateMsg(int pmid, int touid, String formHash, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .deleteSinglePrivateMsg(formHash, "pmdeletehk_" + pmid, touid, pmid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

//    public void uploadImg(String module,
//                          String type,
//                          String token,
//                          String secret,
//                          List<MultipartBody.Part> imgs,
//                          Observer<UploadResultBean> observer) {
//        Call<UploadResultBean> observable = RetrofitCookieUtil
//                .getInstance()
//                .getApiService()
//                .uploadImage(module, type, token, secret, imgs);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
//    }
}
