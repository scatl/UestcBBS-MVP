package com.scatl.uestcbbs.module.post.view;

import com.scatl.uestcbbs.entity.FavoritePostResultBean;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.entity.SupportResultBean;
import com.scatl.uestcbbs.entity.VoteResultBean;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/24 14:33
 */
public interface PostDetailView {
    void onGetPostDetailSuccess(PostDetailBean postDetailBean);
    void onGetPostDetailError(String msg);
    void onSupportSuccess(SupportResultBean supportResultBean);
    void onSupportError(String msg);
    void onFavoritePostSuccess(FavoritePostResultBean favoritePostResultBean);
    void onFavoritePostError(String msg);
    void onVoteSuccess(VoteResultBean voteResultBean);
    void onVoteError(String msg);
}
