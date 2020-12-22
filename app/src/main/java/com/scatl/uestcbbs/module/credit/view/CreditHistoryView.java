package com.scatl.uestcbbs.module.credit.view;

import com.scatl.uestcbbs.entity.MineCreditBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/12/12 19:03
 * description:
 */
public interface CreditHistoryView {
    void onGetMineCreditHistorySuccess(List<MineCreditBean.CreditHistoryBean> creditHistoryBeans, boolean hasNext);
    void onGetMineCreditHistoryError(String msg);
}
