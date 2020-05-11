package com.scatl.uestcbbs.module.home.view;

import com.scatl.uestcbbs.entity.GrabSofaBean;

/**
 * author: sca_tl
 * date: 2020/5/1 20:45
 * description:
 */
public interface GrabSofaView {
    void onGrabSofaDataSuccess(GrabSofaBean grabSofaBean);
    void onGrabSofaDataError(String msg);

}
