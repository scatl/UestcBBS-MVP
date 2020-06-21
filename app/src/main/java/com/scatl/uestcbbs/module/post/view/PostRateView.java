package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.RateInfoBean;

/**
 * author: sca_tl
 * date: 2020/6/21 19:54
 * description:
 */
public interface PostRateView {
    void onGetRateInfoSuccess(RateInfoBean rateInfoBean);
    void onGetRateInfoError(String msg);
    void onRateSuccess(String msg);
    void onRateError(String msg);
}
