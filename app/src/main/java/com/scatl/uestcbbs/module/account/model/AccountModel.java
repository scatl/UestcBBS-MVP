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
public class AccountModel {
    public void login(String userName,
                      String userPsw,
                      Observer<Response<ResponseBody>> observer) {
        Observable<Response<ResponseBody>> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .login(userName, userPsw);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getRealNameInfo(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getRealNameInfo();
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

    public void findUserName(String formhash, String student_id, String portal_password, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formhash);
        map.put("student_id", student_id);
        map.put("portal_password", portal_password);
        map.put("resetpasswordsubmit", "1");

        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .findUserName(RetrofitUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void resetPassword(String formhash, String username, String student_name,
                              String newpassword, String newpassword2 ,String student_id, String portal_password,
                              Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formhash);
        map.put("username", username);
        map.put("student_id", student_id);
        map.put("student_name", student_name);
        map.put("portal_password", portal_password);
        map.put("newpassword", newpassword);
        map.put("newpassword2", newpassword2);
        map.put("resetpasswordsubmit", "1");

        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .resetPassword(RetrofitUtil.generateRequestBody(map));
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

}
