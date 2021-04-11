package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.RateUserBean;

import java.util.List;

public interface ViewDaShangView {
    void onGetRateUserSuccess(List<RateUserBean> rateUserBeans);
    void onGetRateUserError(String msg);
}
