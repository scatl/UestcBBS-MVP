package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardListView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

public class BoardListPresenter extends BasePresenter<BoardListView> {

    private BoardModel boardModel = new BoardModel();

    public void getForumList(Context context) {
        boardModel.getForumList(
                new Observer<ForumListBean>() {
                    @Override
                    public void OnSuccess(ForumListBean forumListBean) {
                        if (forumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetBoardListSuccess(forumListBean);
                        }
                        if (forumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetBoardListError(forumListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetBoardListError(e.message);
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

    public void getTotalPosts() {
        boardModel.getTotalPosts(new Observer<String>() {
            @Override
            public void OnSuccess(String s) {

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {

            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

}
