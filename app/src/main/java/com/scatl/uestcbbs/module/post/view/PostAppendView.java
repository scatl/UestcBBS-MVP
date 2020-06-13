package com.scatl.uestcbbs.module.post.view;

/**
 * author: sca_tl
 * date: 2020/5/27 14:09
 * description:
 */
public interface PostAppendView {
    void onGetFormHashSuccess(String formHash);
    void onGetFormHashError(String msg);

    void onPostAppendSuccess(String msg);
    void onPostAppendError(String msg);

    void onSubmitDianPingSuccess(String msg);
    void onSubmitDianPingError(String msg);
}
