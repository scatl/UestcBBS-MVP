package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;

/**
 * author: sca_tl
 * date: 2021/4/4 19:36
 * description:
 */
public interface P2HotCommentView {
    void onGetAllPostSuccess(PostDetailBean postDetailBean);
    void onGetAllPostError(String msg);
    void onAppendPost(int replyPostsId, int tid);
    void onSupportSuccess(SupportResultBean supportResultBean, String action, int position);
    void onSupportError(String msg);
    void onPingFen(int pid);
    void onOnlyReplyAuthor(int uid);
    void onReportSuccess(ReportBean reportBean);
    void onReportError(String msg);
    void onDeletePost(int tid, int pid);
    void onStickReplySuccess(String msg);
    void onStickReplyError(String msg);
}
