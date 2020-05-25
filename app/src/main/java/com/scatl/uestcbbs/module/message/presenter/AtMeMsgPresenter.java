package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.AtMeMsgView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 13:06
 */
public class AtMeMsgPresenter extends BasePresenter<AtMeMsgView> {

    private MessageModel messageModel = new MessageModel();

    public void getAtMeMsg(int page, int pageSize, Context context) {
        messageModel.getAtMeMsg(page, pageSize,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<AtMsgBean>() {
                    @Override
                    public void OnSuccess(AtMsgBean atMsgBean) {
                        if (atMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetAtMeMsgSuccess(atMsgBean);
                        }
                        if (atMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetAtMeMsgError(atMsgBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetAtMeMsgError(e.message);
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
