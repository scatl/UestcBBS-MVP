package com.scatl.uestcbbs.module.search.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.load
import com.scatl.util.ColorUtil

/**
 * Created by sca_tl at 2023/4/4 10:31
 */
class SearchPostAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<SearchPostBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {

    fun addSearchPostData(data: List<SearchPostBean.ListBean>, refresh: Boolean) {
        val newList: MutableList<SearchPostBean.ListBean> = ArrayList()
        for (i in data.indices) {
            if (data[i].user_nick_name.isNullOrEmpty()) {
                data[i].user_nick_name = Constant.ANONYMOUS_NAME
                data[i].avatar = Constant.DEFAULT_AVATAR
                data[i].user_id = 0
            } else {
                data[i].avatar = Constant.USER_AVATAR_URL + data[i].user_id
            }
            newList.add(data[i])
        }

        if (refresh) {
            setNewData(newList)
        } else {
            addData(newList)
        }
    }

    override fun convert(helper: BaseViewHolder, item: SearchPostBean.ListBean) {
        super.convert(helper, item)
        helper
            .setText(R.id.item_simple_post_title, item.title)
            .setText(R.id.item_simple_post_content, item.subject)
            .setText(R.id.item_simple_post_user_name, item.user_nick_name)
            .setText(R.id.item_simple_post_comments_count, "  " + item.replies)
            .setText(R.id.item_simple_post_view_count, "  " + item.hits)
            .setText(R.id.item_simple_post_time, TimeUtil.formatTime(item.last_reply_date, R.string.reply_time, mContext))
            .addOnClickListener(R.id.item_simple_post_user_avatar)

        helper.getView<MaterialCardView>(R.id.item_simple_post_card_view)
            .setCardBackgroundColor(ColorUtil.getAttrColor(mContext, R.attr.colorOnSurfaceInverse))
        helper.getView<View>(R.id.item_simple_post_board_name).visibility = View.GONE
        helper.getView<View>(R.id.item_simple_post_poll_rl).visibility =
            if (item.vote == 1) View.VISIBLE else View.GONE

        val avatarImg = helper.getView<ImageView>(R.id.item_simple_post_user_avatar)
        if (item.user_id == 0 && "匿名" == item.user_nick_name) {
            avatarImg.load(R.drawable.ic_anonymous)
        } else {
            avatarImg.load(item.avatar)
        }
    }
}