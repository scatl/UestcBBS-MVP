package com.scatl.uestcbbs.module.magic.view;

import com.scatl.uestcbbs.entity.MagicDetailBean;

public interface MagicDetailView {
    void onGetMagicDetailSuccess(MagicDetailBean magicDetailBean, String formhash);
    void onGetMagicDetailError(String msg);
    void onBuyMagicSuccess(String msg);
    void onBuyMagicError(String msg);
}
