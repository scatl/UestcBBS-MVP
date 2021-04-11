package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ForumUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * date: 2021/4/6 20:12
 * description:
 */
public interface ViewDianPingView {
    void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext);
    void onGetPostDianPingListError(String msg);
}
