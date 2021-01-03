package com.scatl.uestcbbs.module.user.presenter;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.user.model.UserModel;
import com.scatl.uestcbbs.module.user.view.UserVisitorView;

import io.reactivex.disposables.Disposable;

public class UserVisitorPresenter extends BasePresenter<UserVisitorView> {
    UserModel userModel = new UserModel();

    public void deleteVisitedHistory(int uid, int position) {
        userModel.deleteVisitedHistory(uid, new Observer<String>() {
            @Override
            public void OnSuccess(String s) {
                view.onDeleteVisitedHistorySuccess("删除记录成功", position);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDeleteVisitedHistoryError("删除失败：" + e.message);
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
