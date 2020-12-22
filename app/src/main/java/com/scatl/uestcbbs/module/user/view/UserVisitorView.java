package com.scatl.uestcbbs.module.user.view;

public interface UserVisitorView {
    void onDeleteVisitedHistorySuccess(String msg, int position);
    void onDeleteVisitedHistoryError(String msg);
}
