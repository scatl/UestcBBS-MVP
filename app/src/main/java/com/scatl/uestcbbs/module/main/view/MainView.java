package com.scatl.uestcbbs.module.main.view;

import com.scatl.uestcbbs.entity.OpenPicBean;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:17
 */
public interface MainView {
    void getUpdateSuccess(UpdateBean updateBean);
    void getUpdateFail(String msg);
    void getSettingsSuccess(SettingsBean settingsBean);
    void getSettingsFail(String msg);
    void getOpenPicSuccess(OpenPicBean openPicBean);
    void getOpenPicsFail(String msg);
}
