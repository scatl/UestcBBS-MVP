package com.scatl.uestcbbs.module.darkroom.view;

import com.scatl.uestcbbs.entity.DarkRoomBean;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/4/10 12:07
 * description:
 */
public interface DarkRoomView {
    void onGetDarkRoomDataSuccess(List<DarkRoomBean> darkRoomBeanList);
    void onGetDarkRoomDataError(String msg);
}
