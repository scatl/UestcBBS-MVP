package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.PostDetailBean;

/**
 * author: sca_tl
 * date: 2021/3/31 19:49
 * description:
 */
public interface P2DianZanView {
    void onGetPostDetailSuccess(PostDetailBean postDetailBean);
    void onGetPostDetailError(String msg);
}
