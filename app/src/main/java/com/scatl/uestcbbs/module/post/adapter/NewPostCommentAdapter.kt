package com.scatl.uestcbbs.module.post.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.helper.PreloadAdapter

/**
 * Created by sca_tl at 2023/5/19 17:16
 */
class NewPostCommentAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null) :
    PreloadAdapter<PostDetailBean.ListBean, BaseViewHolder>(layoutResId, onPreload) {
}