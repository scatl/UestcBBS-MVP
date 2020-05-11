package com.scatl.uestcbbs.services.heartmsg.presenter;

import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HeartMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.services.heartmsg.model.HeartMsgModel;
import com.scatl.uestcbbs.services.heartmsg.view.HeartMsgView;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 13:46
 */
public class HeartMsgPresenter extends BasePresenter<HeartMsgView> {

    private HeartMsgModel heartMsgModel = new HeartMsgModel();

    public void getHeartMsg(String token, String secret, String sdkVersion) {
        heartMsgModel.getHeartMsg(token, secret, sdkVersion,new Observer<HeartMsgBean>() {
            @Override
            public void OnSuccess(HeartMsgBean heartMsgBean) {
                if (heartMsgBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                    view.onGetHeartMsgSuccess(heartMsgBean);
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
                SubscriptionManager.getInstance().add(d);
            }
        });
    }

}
