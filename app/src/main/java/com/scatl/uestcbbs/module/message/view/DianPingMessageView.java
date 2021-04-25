package com.scatl.uestcbbs.module.message.view;

import com.scatl.uestcbbs.entity.DianPingMessageBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/4/18 17:48
 * description:
 */
public interface DianPingMessageView {
    void onGetDianPingMessageSuccess(List<DianPingMessageBean> dianPingMessageBean, boolean hasNext);
    void onGetDianPingMessageError(String msg);
}
