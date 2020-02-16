package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.ReplyMeMsgBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/29 15:22
 */
public interface ReplyMeMsgView {
    void onGetReplyMeMsgSuccess(ReplyMeMsgBean replyMeMsgBean);
    void onGetReplyMeMsgError(String msg);
}
