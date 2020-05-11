package com.scatl.uestcbbs.module.home.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.GrabSofaBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.home.model.HomeModel;
import com.scatl.uestcbbs.module.home.view.GrabSofaView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.FileUtil;
import com.thoughtworks.xstream.XStream;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/5/1 20:45
 * description:
 */
public class GrabSofaPresenter extends BasePresenter<GrabSofaView> {

    private HomeModel homeModel = new HomeModel();

    public void getGrabSofaData() {
        homeModel.getGrabSofa(new Observer<String>() {
            @Override
            public void OnSuccess(String data) {

                XStream xStream = new XStream();
                xStream.processAnnotations(GrabSofaBean.class);
                GrabSofaBean grabSofaBean = (GrabSofaBean) xStream.fromXML(data);

                view.onGrabSofaDataSuccess(grabSofaBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGrabSofaDataError(e.message);
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
