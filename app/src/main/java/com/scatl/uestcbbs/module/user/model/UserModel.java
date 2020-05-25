package com.scatl.uestcbbs.module.user.model;

import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.entity.UserDetailBean;
import com.scatl.uestcbbs.entity.UserFriendBean;
import com.scatl.uestcbbs.entity.UserPostBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class UserModel {
    public void getAtUserList(int page,
                              int pageSize,
                              String token,
                              String secret,
                              Observer<AtUserListBean> observer) {
        Observable<AtUserListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .atUserList(page, pageSize, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserDetail(int userId,
                              String token,
                              String secret,
                              Observer<UserDetailBean> observer) {
        Observable<UserDetailBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userDetail(userId, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserPost(int page,
                            int pageSize,
                            int uid,
                             String type,
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

    public void followUser(int uid,
                            String type,
                            String token,
                            String secret,
                            Observer<FollowUserBean> observer) {
        Observable<FollowUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .followUser(uid, type, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void blackUser(int uid,
                           String type,
                           String token,
                           String secret,
                           Observer<BlackUserBean> observer) {
        Observable<BlackUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .blackUser(uid, type, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void getUserFriend(
                            int page,
                            int pageSize,
                            int uid,
                              String type,
                              String token,
                              String secret,
                              Observer<UserFriendBean> observer) {
        Observable<UserFriendBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userFriend(page, pageSize, uid, type, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void modifySign(
                            String type,
                            String sign,
                            String token,
                            String secret,
                            Observer<ModifySignBean> observer) {
        Observable<ModifySignBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .modifySign(type, sign, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void modifyPsw(
                            String type,
                            String oldPsw,
                            String newPsw,
                            String token,
                            String secret,
                            Observer<ModifyPswBean> observer) {
        Observable<ModifyPswBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .modifyPsw(type, oldPsw, newPsw, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getAlbumList(
                        int uid,
                        String token,
                        String secret,
                        Observer<AlbumListBean> observer) {
        Observable<AlbumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .albumList(uid, 1, 1000, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getPhotoList(
                        int uid,
                        int albumId,
                        String token,
                        String secret,
                        Observer<PhotoListBean> observer) {
        Observable<PhotoListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .photoList(uid, albumId, 1, 1000, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
