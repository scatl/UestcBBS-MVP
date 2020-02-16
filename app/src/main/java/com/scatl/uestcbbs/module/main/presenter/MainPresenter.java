package com.scatl.uestcbbs.module.main.presenter;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.main.model.MainModel;
import com.scatl.uestcbbs.module.main.view.MainView;
import com.scatl.uestcbbs.services.heartmsg.view.HeartMsgService;
import com.scatl.uestcbbs.util.ServiceUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:16
 */
public class MainPresenter extends BasePresenter<MainView> {

    private MainModel mainModel = new MainModel();

    public void getUpdate() {
        mainModel.getUpdate(new Observer<UpdateBean>() {
            @Override
            public void OnSuccess(UpdateBean updateBean) {
                view.getUpdateSuccess(updateBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getUpdateFail(e.message);
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

    public void startService(Context context) {
        if (SharePrefUtil.isLogin(context)) {
            if (! ServiceUtil.isServiceRunning(context, HeartMsgService.serviceName)) {
                Intent intent = new Intent(context, HeartMsgService.class);
                context.startService(intent);
            }
        }
    }

}
