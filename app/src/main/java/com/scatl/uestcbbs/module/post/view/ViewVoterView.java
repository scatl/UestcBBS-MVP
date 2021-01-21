package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.ViewVoterBean;
import com.scatl.uestcbbs.entity.VoteOptionsBean;

import java.util.List;

public interface ViewVoterView {
    void onGetVoteOptionsSuccess(List<VoteOptionsBean> voteOptionsBeans);
    void onGetVoteOptionsError(String msg);
    void onGetVotersSuccess(List<ViewVoterBean> viewVoterBeans, boolean hasNext);
    void onGetVotersError(String msg);
}
