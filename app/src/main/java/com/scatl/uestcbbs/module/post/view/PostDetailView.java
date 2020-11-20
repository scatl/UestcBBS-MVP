package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:33
 */
public interface PostDetailView {
    void onGetPostDetailSuccess(PostDetailBean postDetailBean);
    void onGetPostDetailError(String msg);
    void onSupportSuccess(SupportResultBean supportResultBean, String type, int position);
    void onSupportError(String msg);
    void onFavoritePostSuccess(FavoritePostResultBean favoritePostResultBean);
    void onFavoritePostError(String msg);
    void onVoteSuccess(VoteResultBean voteResultBean);
    void onVoteError(String msg);
    void onReportSuccess(ReportBean reportBean);
    void onReportError(String msg);
    void onGetNewVoteDataSuccess(PostDetailBean.TopicBean.PollInfoBean pollInfoBean);
    void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext);
    void onGetPostDianPingListError(String msg);
    void onGetPostWebDetailSuccess(String favoriteNum, String formHash);
    void onStickReplySuccess(String msg);
    void onStickReplyError(String msg);
    void onPingFen(int pid);
    void onOnlyReplyAuthor(int uid);
    void onAppendPost(int replyPostsId, int tid);
}
