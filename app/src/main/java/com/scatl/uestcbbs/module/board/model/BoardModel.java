package com.scatl.uestcbbs.module.board.model;

import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.SimplePostListBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.util.RetrofitCookieUtil;
import com.scatl.uestcbbs.util.RetrofitUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/30 15:01
 */
public class BoardModel {
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

    public void getTotalPosts(Observer<String> observer) {
        Observable<String> observable = RetrofitUtil
                .getInstance()
                .getApiService()
                .getHomeInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getForumDetail(int fid, Observer<String> observer) {
        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .getForumDetail(fid);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void payForVisiting(int fid, String formhash, Observer<String> observer) {
        Map<String, String> map = new HashMap<>();
        map.put("formhash", formhash);
        map.put("loginsubmit", "true");

        Observable<String> observable = RetrofitCookieUtil
                .getInstance()
                .getApiService()
                .payForVisitingForum(fid, RetrofitCookieUtil.generateRequestBody(map));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
