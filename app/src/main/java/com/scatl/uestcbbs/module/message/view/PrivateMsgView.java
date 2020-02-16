package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.PrivateMsgBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 16:31
 */
public interface PrivateMsgView {
    void onGetPrivateMsgSuccess(PrivateMsgBean privateMsgBean);
    void onGetPrivateMsgError(String msg);
}
