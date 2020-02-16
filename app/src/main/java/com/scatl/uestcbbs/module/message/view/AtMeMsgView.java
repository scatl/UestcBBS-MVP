package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.AtMsgBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 13:05
 */
public interface AtMeMsgView {
    void onGetAtMeMsgSuccess(AtMsgBean atMsgBean);
    void onGetAtMeMsgError(String msg);
}
