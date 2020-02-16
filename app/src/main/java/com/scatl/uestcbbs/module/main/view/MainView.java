package com.scatl.uestcbbs.module.main.view;

import com.scatl.uestcbbs.entity.UpdateBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:17
 */
public interface MainView {
    void getUpdateSuccess(UpdateBean updateBean);
    void getUpdateFail(String msg);
}
