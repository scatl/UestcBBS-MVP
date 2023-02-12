package com.scatl.uestcbbs.module.search.model;

import com.scatl.uestcbbs.entity.ModifySignBean;
import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.entity.SearchUserBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SearchModel {

    public void searchUser(int page,
                           int pageSize,
                           int searchId,
                           String keyword,
                           String token,
                           String secret,
                           Observer<SearchUserBean> observer) {
        Observable<SearchUserBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .searchUser(page, pageSize, searchId, keyword, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void searchPost(int page,
                           int pageSize,
                           String keyword,
                           String token,
                           String secret,
                           Observer<SearchPostBean> observer) {
        Observable<SearchPostBean> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .searchPost(page, pageSize, keyword, token, secret);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
