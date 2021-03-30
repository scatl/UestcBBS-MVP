package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.PostDianPingBean;

import java.util.List;

public interface P2DianPingView {
    void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext);
    void onGetPostDianPingListError(String msg);
}
