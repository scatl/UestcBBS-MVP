package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.PrivateMsgBean;

public interface MessageView {
    void onGetPrivateMsgSuccess(PrivateMsgBean privateMsgBean);
    void onGetPrivateMsgError(String msg);

    void onDeletePrivateMsgSuccess(String msg, int position);
    void onDeletePrivateMsgError(String msg);
}
