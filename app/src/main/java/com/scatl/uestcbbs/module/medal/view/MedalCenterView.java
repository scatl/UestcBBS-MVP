package com.scatl.uestcbbs.module.medal.view;

import com.scatl.uestcbbs.entity.MedalBean;

/**
 * author: sca_tl
 * date: 2020/10/7 18:51
 * description:
 */
public interface MedalCenterView {
    void onGetMedalCenterDataSuccess(MedalBean medalBean);
    void onGetMedalCenterDataError(String msg);

}
