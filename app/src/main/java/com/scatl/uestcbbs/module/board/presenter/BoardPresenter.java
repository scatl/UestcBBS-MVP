package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

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
//                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    /**
     * author: sca_tl
     * description: 请求权限
     */
    public void requestPermission(FragmentActivity activity, final int action, String... permissions) {
        CommonUtil.requestPermission(activity, new OnPermission() {
            @Override
            public void onGranted() {
                view.onPermissionGranted(action);
            }

            @Override
            public void onRefusedWithNoMoreRequest() {
                view.onPermissionRefusedWithNoMoreRequest();
            }

            @Override
            public void onRefused() {
                view.onPermissionRefused();
            }
        }, permissions);
    }

}
