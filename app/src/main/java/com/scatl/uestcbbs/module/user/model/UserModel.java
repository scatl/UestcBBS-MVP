package com.scatl.uestcbbs.module.user.model;

import com.scatl.uestcbbs.entity.AlbumListBean;
import com.scatl.uestcbbs.entity.AtUserListBean;
import com.scatl.uestcbbs.entity.BlackUserBean;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.FollowUserBean;
import com.scatl.uestcbbs.entity.ModifyPswBean;
import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.PhotoListBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
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
                              Observer<AtUserListBean> observer) {
        Observable<AtUserListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .atUserList(page, pageSize);
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

    public void getUserPost(int page,
                            int pageSize,
                            int uid,
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

    public void followUser(int uid,
                           String type,
                           Observer<FollowUserBean> observer) {
        Observable<FollowUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .followUser(uid, type);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void blackUser(int uid,
                          String type,
                          Observer<BlackUserBean> observer) {
        Observable<BlackUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .blackUser(uid, type);
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
            Observer<UserFriendBean> observer) {
        Observable<UserFriendBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .userFriend(page, pageSize, uid, type);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void modifySign(String type,
                           String sign,
                           Observer<ModifySignBean> observer) {
        Observable<ModifySignBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .modifySign(type, sign);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void modifyPsw(
            String type,
            String oldPsw,
            String newPsw,
            Observer<ModifyPswBean> observer) {
        Observable<ModifyPswBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .modifyPsw(type, oldPsw, newPsw);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getAlbumList(int uid,
                             Observer<AlbumListBean> observer) {
        Observable<AlbumListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .albumList(uid, 1, 1000);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getPhotoList(int uid,
                             int albumId,
                             Observer<PhotoListBean> observer) {
        Observable<PhotoListBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .photoList(uid, albumId, 1, 1000);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getAccountBlackList(int page, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getAccountBlackList(page);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getModifyAvatarParams(Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getModifyAvatarPara();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void modifyAvatar(String agent, String input, String avatar1, String avatar2, String avatar3, Observer<String> observer) {

        Map<String, String> map = new HashMap<>();
        map.put("avatar1", avatar1);
        map.put("avatar2", avatar2);
        map.put("avatar3", avatar3);

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .modifyAvatar(agent, input, RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserSpace(int uid, String doo, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .userSpace(uid, doo);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void deleteVisitedHistory(int uid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .deleteVisitedHistory(uid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void searchUser(int page, int pageSize, int searchId, String keyword, Observer<SearchUserBean> observer) {
        Observable<SearchUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .searchUser(page, pageSize, searchId, keyword);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserSpaceByName(String name, Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getUserSpaceByName(name);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
