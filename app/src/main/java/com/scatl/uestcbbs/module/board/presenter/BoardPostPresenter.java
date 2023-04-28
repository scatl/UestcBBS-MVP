package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;
import android.util.Log;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardPostView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.disposables.Disposable;

public class BoardPostPresenter extends BasePresenter<BoardPostView> {

    private BoardModel boardModel = new BoardModel();

    public void getBoardPostList(int page,
                                 int pageSize,
                                 int topOrder,
                                 int boardId,
                                 int filterId,
                                 String filterType,
                                 String sortby,
                                 Context context) {
        boardModel.getSingleBoardPostList(page, pageSize,
                topOrder, boardId, filterId, filterType, sortby,
                new Observer<CommonPostBean>() {
                    @Override
                    public void OnSuccess(CommonPostBean singleBoardBean) {
                        if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetBoardPostSuccess(singleBoardBean);
                        }
                        if (singleBoardBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetBoardPostError(singleBoardBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetBoardPostError(e.message);
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

    public void payForVisiting(int fid, String formhash) {
        boardModel.payForVisiting(fid, formhash, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                if (s.contains("支付成功")) {
                    view.onPaySuccess("支付成功。请【返回】或者【或者重新登录】再进入该页面，可能需要稍等一会才能浏览！");
                } else {
                    try {
                        Document document = Jsoup.parse(s);
                        String info = document.select("div[id=messagetext]").text();
                        view.onPayError("支付失败:" + info);
                    } catch (Exception e) {
                        view.onPayError("支付失败:" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onPayError("支付失败：" + e.message);
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
