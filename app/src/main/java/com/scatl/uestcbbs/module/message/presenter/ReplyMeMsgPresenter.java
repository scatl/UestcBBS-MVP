package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AtMsgBean;
import com.scatl.uestcbbs.entity.ReplyMeMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.ReplyMeMsgView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 15:24
 */
public class ReplyMeMsgPresenter extends BasePresenter<ReplyMeMsgView> {
    private MessageModel messageModel = new MessageModel();

    public void getReplyMeMsg(int page, int pageSize, Context context) {
        messageModel.getReplyMsg(page, pageSize,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<ReplyMeMsgBean>() {
                    @Override
                    public void OnSuccess(ReplyMeMsgBean replyMeMsgBean) {
                        if (replyMeMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetReplyMeMsgSuccess(replyMeMsgBean);
                        }
                        if (replyMeMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetReplyMeMsgError(replyMeMsgBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetReplyMeMsgError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }
}
