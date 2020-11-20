package com.scatl.uestcbbs.module.magic.view;

import com.scatl.uestcbbs.entity.UseMagicBean;

public interface UseMagicView {
    void onGetUseMagicDetailSuccess(UseMagicBean useMagicBean, String formhash);
    void onGetUseMagicDetailError(String msg);
    void onUseMagicSuccess(String msg);
    void onUseMagicError(String msg);
}
