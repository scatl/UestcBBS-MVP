package com.scatl.uestcbbs.module.search.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/4 10:10
 */
class SearchUserAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<SearchUserBean.BodyBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: SearchUserBean.BodyBean.ListBean) {
        super.convert(helper, item)
        helper
            .setText(R.id.search_user_name, item.name)
            .setText(R.id.search_user_last_login, TimeUtil.formatTime(item.dateline, R.string.last_login_time, mContext))

        helper.getView<ImageView>(R.id.search_user_icon).load(item.icon)
    }

}