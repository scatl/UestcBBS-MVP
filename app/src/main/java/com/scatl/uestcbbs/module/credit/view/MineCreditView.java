package com.scatl.uestcbbs.module.credit.view;

import com.scatl.uestcbbs.entity.MineCreditBean;

import java.util.List;

public interface MineCreditView {
    void onGetMineCreditSuccess(MineCreditBean mineCreditBean, String formHash);

}
