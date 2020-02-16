package com.scatl.uestcbbs.module.search.view;

import com.scatl.uestcbbs.entity.SearchPostBean;
import com.scatl.uestcbbs.entity.SearchUserBean;

public interface SearchView {
    void onSearchUserSuccess(SearchUserBean searchUserBean);
    void onSearchUserError(String msg);
    void onSearchPostSuccess(SearchPostBean searchPostBean);
    void onSearchPostError(String msg);
}
