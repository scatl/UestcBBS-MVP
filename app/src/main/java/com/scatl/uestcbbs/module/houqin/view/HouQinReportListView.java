package com.scatl.uestcbbs.module.houqin.view;

import com.scatl.uestcbbs.entity.HouQinReportListBean;

/**
 * author: sca_tl
 * date: 2020/10/24 11:18
 * description:
 */
public interface HouQinReportListView {
    void onGetReportListSuccess(HouQinReportListBean houQinReportListBean);
    void onGetReportListError(String msg);
}
