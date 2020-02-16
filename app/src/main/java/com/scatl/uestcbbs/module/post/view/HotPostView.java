package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.HotPostBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/12 17:40
 */
public interface HotPostView {
    void getHotPostDataSuccess(HotPostBean hotPostBean);
    void getHotPostDataError(String msg);
}
