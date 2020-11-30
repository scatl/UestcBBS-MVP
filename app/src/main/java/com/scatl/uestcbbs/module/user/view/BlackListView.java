package com.scatl.uestcbbs.module.user.view;

import com.scatl.uestcbbs.entity.BlackListBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/11/28 13:07
 * description:
 */
public interface BlackListView {
    void onGetBlackListSuccess(List<BlackListBean> blackListBeans, boolean hasNext);
    void onGetBlackListError(String msg);
}
