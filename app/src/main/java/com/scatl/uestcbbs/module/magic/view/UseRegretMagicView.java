package com.scatl.uestcbbs.module.magic.view;

import com.scatl.uestcbbs.entity.UseRegretMagicBean;

public interface UseRegretMagicView {
    void onGetMagicDetailSuccess(UseRegretMagicBean useRegretMagicBean, String formhash);
    void onGetMagicDetailError(String msg);
    void onUseMagicSuccess(String msg);
    void onUseMagicError(String msg);
}
