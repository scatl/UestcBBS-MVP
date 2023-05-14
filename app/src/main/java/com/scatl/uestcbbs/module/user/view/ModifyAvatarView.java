package com.scatl.uestcbbs.module.user.view;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

public interface ModifyAvatarView {
    void onGetParaSuccess(String agent, String input);
    void onGetParaError(String msg);
    void onUploadSuccess(String msg);
    void onUploadError(String msg);
}
