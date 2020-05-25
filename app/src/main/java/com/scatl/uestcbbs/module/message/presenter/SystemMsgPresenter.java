package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.SystemMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.SystemMsgView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:48
 */
public class SystemMsgPresenter extends BasePresenter<SystemMsgView> {

    private MessageModel messageModel = new MessageModel();

    public void getSystemMsg(int page, int pageSize, Context context) {
        messageModel.getSystemMsg(page, pageSize,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SystemMsgBean>() {
                    @Override
                    public void OnSuccess(SystemMsgBean systemMsgBean) {
                        if (systemMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetSystemMsgSuccess(systemMsgBean);
                        }
                        if (systemMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetSystemMsgError(systemMsgBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetSystemMsgError(e.message);
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

}
