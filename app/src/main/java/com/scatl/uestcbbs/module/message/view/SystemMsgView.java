package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.SystemMsgBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 15:49
 */
public interface SystemMsgView {
    void onGetSystemMsgSuccess(SystemMsgBean systemMsgBean);
    void onGetSystemMsgError(String msg);
}
