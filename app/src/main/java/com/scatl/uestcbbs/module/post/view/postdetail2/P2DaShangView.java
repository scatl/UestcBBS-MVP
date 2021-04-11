package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.RateUserBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/3/31 20:19
 * description:
 */
public interface P2DaShangView {
    void onGetRateUserSuccess(List<RateUserBean> rateUserBeans);
    void onGetRateUserError(String msg);
}
