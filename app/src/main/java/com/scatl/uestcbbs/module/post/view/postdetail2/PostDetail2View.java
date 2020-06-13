package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.PostDetailBean;

/**
 * author: sca_tl
 * date: 2020/6/6 19:15
 * description:
 */
public interface PostDetail2View {
    void onGetPostDetailSuccess(PostDetailBean postDetailBean);
    void onGetPostDetailError(String msg);
}
