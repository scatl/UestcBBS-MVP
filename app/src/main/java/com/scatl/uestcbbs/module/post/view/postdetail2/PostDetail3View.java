package com.scatl.uestcbbs.module.post.view.postdetail2;

import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.PostDianPingBean;
import com.scatl.uestcbbs.entity.ReportBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;

import java.util.List;

public interface PostDetail3View {
    void onGetPostDetailSuccess(PostDetailBean postDetailBean);
    void onGetPostDetailError(String msg);
    void onVoteSuccess(VoteResultBean voteResultBean);
    void onVoteError(String msg);
    void onGetNewVoteDataSuccess(PostDetailBean.TopicBean.PollInfoBean pollInfoBean);
    void onGetPostWebDetailSuccess(String favoriteNum, String rewardInfo, String shengYuReword, String formHash, boolean originalCreate, boolean essence);
    void onGetAllPostSuccess(PostDetailBean postDetailBean);
    void onGetAllPostError(String msg);
    void onGetPostDianPingListSuccess(List<PostDianPingBean> commentBeans, boolean hasNext);
    void onGetPostDianPingListError(String msg);

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
