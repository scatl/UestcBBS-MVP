package com.scatl.uestcbbs.module.post.view;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 20:39
 */
public interface PostDraftView {
    void onDeleteConfirm(int position);
    void onDeleteAllSuccess(String msg);
    void onDeleteAllError(String msg);
}
