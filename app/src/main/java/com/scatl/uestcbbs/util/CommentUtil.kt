package com.scatl.uestcbbs.util

import com.scatl.uestcbbs.entity.PostDetailBean

object CommentUtil {

    @JvmStatic
    fun findCommentByPid(listBean: List<PostDetailBean.ListBean>, pid: String?): PostDetailBean.ListBean? {
        for (i in listBean.indices) {
            val bean = listBean[i]
            if (pid == bean.reply_posts_id.toString()) {
                return bean
            }
        }
        return null
    }

    @JvmStatic
    fun getFloorInFloorCommentData(postDetailBean: PostDetailBean): List<PostDetailBean.ListBean>? {
        val result: MutableList<PostDetailBean.ListBean> = ArrayList()
        val hasQuote: MutableList<PostDetailBean.ListBean> = ArrayList()
        for (i in postDetailBean.list.indices) {
            val listBean = postDetailBean.list[i]
            if (listBean.is_quote == 1 && listBean.quote_pid != "0") {
                hasQuote.add(listBean)
            } else {
                result.add(listBean)
            }
        }
        for (p in hasQuote) {
            val root = findRootComment(postDetailBean.list, p)
            if (root != null) {
                if (root.quote_comments == null) {
                    root.quote_comments = ArrayList()
                }
                root.quote_comments.add(p)
            }
        }
        return result
    }

    @JvmStatic
    fun findRootComment(listBean: List<PostDetailBean.ListBean>, comment: PostDetailBean.ListBean?): PostDetailBean.ListBean? {
        var root = comment
        root = if (comment != null && comment.is_quote == 1 && comment.quote_pid != "0") {
            findRootComment(listBean, findCommentByPid(listBean, comment.quote_pid))
        } else {
            return root
        }
        return root
    }
}