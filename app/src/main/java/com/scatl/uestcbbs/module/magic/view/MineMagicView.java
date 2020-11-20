package com.scatl.uestcbbs.module.magic.view;

import com.scatl.uestcbbs.entity.MineMagicBean;

public interface MineMagicView {
    void onGetMineMagicSuccess(MineMagicBean mineMagicBean);
    void onGetMineMagicError(String msg);
}
