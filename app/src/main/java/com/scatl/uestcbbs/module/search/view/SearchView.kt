package com.scatl.uestcbbs.module.search.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.entity.SearchUserBean

/**
 * Created by sca_tl at 2023/2/8 10:22
 */
interface SearchView: BaseView {
    fun onSearchUserSuccess(searchUserBean: SearchUserBean)
    fun onSearchUserError(msg: String?)
    fun onSearchPostSuccess(searchPostBean: SearchPostBean)
    fun onSearchPostError(msg: String?)
}