package com.scatl.uestcbbs.module.houqin.presenter;

import android.util.Log;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.HouQinReportListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.houqin.model.HouQinModel;
import com.scatl.uestcbbs.module.houqin.view.HouQinReportListView;
import com.scatl.uestcbbs.util.CommonUtil;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2020/10/24 11:18
 * description:
 */
public class HouQinReportListPresenter extends BasePresenter<HouQinReportListView> {
    HouQinModel houQinModel = new HouQinModel();

    public void getAllReportList(int pageNo) {
        houQinModel.getAllReportList(pageNo, new Observer<HouQinReportListBean>() {
            @Override
            public void OnSuccess(HouQinReportListBean houQinReportListBean) {
                view.onGetReportListSuccess(houQinReportListBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onGetReportListError("获取列表失败：" + e.message);
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
