package com.scatl.uestcbbs.module.setting.view;

import com.scatl.uestcbbs.entity.UpdateBean;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/15 16:54
 */
public interface SettingsView {
    void getUpdateSuccess(UpdateBean updateBean);
    void getUpdateFail(String msg);
    void getCacheSizeSuccess(String msg);
    void getCacheSizeFail(String msg);
}
