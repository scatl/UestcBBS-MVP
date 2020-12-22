package com.scatl.uestcbbs.module.credit.view;

public interface CreditTransferView {
    void onGetFormHashSuccess(String formHash);
    void onGetFormHashError(String msg);
    void onTransferSuccess(String msg);
    void onTransferError(String msg);
}
