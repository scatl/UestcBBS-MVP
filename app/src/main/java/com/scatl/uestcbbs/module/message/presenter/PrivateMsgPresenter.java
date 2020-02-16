package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.PrivateMsgView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 16:33
 */
public class PrivateMsgPresenter extends BasePresenter<PrivateMsgView> {

    private MessageModel messageModel = new MessageModel();

    public void getPrivateMsg(int page, int pageSize, Context context) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", page);
        jsonObject.put("pageSize", pageSize);
        messageModel.getPrivateMsg(jsonObject.toString(),
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<PrivateMsgBean>() {
                    @Override
                    public void OnSuccess(PrivateMsgBean privateMsgBean) {
                        if (privateMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetPrivateMsgSuccess(privateMsgBean);
                        }
                        if (privateMsgBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetPrivateMsgError(privateMsgBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetPrivateMsgError(e.message);
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
