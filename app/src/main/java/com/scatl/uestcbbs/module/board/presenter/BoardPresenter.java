package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.ForumDetailBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class BoardPresenter extends BasePresenter<BoardView> {

    private BoardModel boardModel = new BoardModel();

    public void getSubBoardList(int fid, Context context) {
        boardModel.getSubForumList(fid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SubForumListBean>() {
                    @Override
                    public void OnSuccess(SubForumListBean subForumListBean) {
                        if (subForumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetSubBoardListSuccess(subForumListBean);
                        }
                        if (subForumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetSubBoardListError(subForumListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetSubBoardListError(e.message);
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

    public void getForumDetail(Context context, int fid) {
        boardModel.getForumDetail(fid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                try {

                    Document document = Jsoup.parse(s);

                    String formhash = document.select("div[class=hdc]").select("div[class=wp]").select("div[class=cl]").select("form[id=scbar_form]").select("input[name=formhash]").attr("value");
                    SharePrefUtil.setForumHash(context, formhash);

                    ForumDetailBean forumDetailBean = new ForumDetailBean();
//                    forumDetailBean.todayPosts = Integer.parseInt(document.select("span[class=xs1 xw0 i]").select("strong[class=xi1]").get(0).ownText());
//                    forumDetailBean.totalPosts = Integer.parseInt(document.select("span[class=xs1 xw0 i]").select("strong[class=xi1]").get(1).ownText());
//                    forumDetailBean.rank = Integer.parseInt(document.select("span[class=xs1 xw0 i]").select("strong[class=xi1]").get(2).ownText());

                    Elements ee = document.select("div[class=bm_c cl pbn]").select("span[class=xi2]");
                    if (!ee.isEmpty()) {
                        forumDetailBean.admins = ee.get(0).select("a").eachText();
                    }


                    view.onGetForumDetailSuccess(forumDetailBean);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

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

}
