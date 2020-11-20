package com.scatl.uestcbbs.module.magic.view;

import com.scatl.uestcbbs.entity.MagicShopBean;
import com.scatl.uestcbbs.entity.MineMagicBean;

public interface MagicShopView {
    void onGetMagicShopSuccess(MagicShopBean magicShopBean);
    void onGetMagicShopError(String msg);
}
