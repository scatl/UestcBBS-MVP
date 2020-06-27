package com.scatl.uestcbbs.module.account.model;

import com.scatl.uestcbbs.entity.HotPostBean;
import com.scatl.uestcbbs.entity.LoginBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:05
 */
public class LoginModel {
    public void simpleLogin(String userName,
                            String userPsw,
                            Observer<LoginBean> observer) {
        Observable<LoginBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .login(userName, userPsw);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loginForCookies(String userName, String userPsw, int answerId, String answer, Observer<Response<ResponseBody>> observer) {

        Map<String, String> map = new HashMap<>();
        map.put("fastloginfiled", "username");
        map.put("questionid", answerId + "");
        map.put("answer", answerId == 0 ? "" : answer);
        map.put("cookietime", "2592000");
        map.put("username", userName);
        map.put("password", userPsw);

        Observable<Response<ResponseBody>> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .loginForCookies(RetrofitUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUploadHash(int topicId, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getUploadHash(topicId);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
