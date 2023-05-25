package com.scatl.uestcbbs.util

import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.manager.BlackListManager

object CommentUtil {

    @JvmStatic
    fun findCommentByPid(listBean: List<PostDetailBean.ListBean>?, pid: String?): PostDetailBean.ListBean? {
        if (listBean == null) {
            return null
        }
        for (i in listBean.indices) {
            val bean = listBean[i]
            if (pid == bean.reply_posts_id.toString()) {
                return bean
            }
        }
        return null
    }

    @JvmStatic
    fun getIndexByPid(listBean: List<PostDetailBean.ListBean>?, pid: String?): Int? {
        if (listBean == null) {
            return null
        }
        for ((index, item) in listBean.withIndex()) {
            if (pid == item.reply_posts_id.toString()) {
                return index
            }
        }
        return null
    }

    @JvmStatic
    fun getIndexByFloor(listBean: List<PostDetailBean.ListBean>?, floor: String?): Int? {
        if (listBean == null) {
            return null
        }
        for ((index, item) in listBean.withIndex()) {
            if (floor == item.position.toString()) {
                return index
            }
        }
        return null
    }

    @JvmStatic
    fun getFloorInFloorCommentData(postDetailBean: PostDetailBean): List<PostDetailBean.ListBean> {
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

    @JvmStatic
    fun getHotComment(postDetailBean: PostDetailBean): List<PostDetailBean.ListBean> {
        val hot: MutableList<PostDetailBean.ListBean> = ArrayList()
        for (i in postDetailBean.list.indices) {
            val item = postDetailBean.list[i]
            if ("support" == item.extraPanel.getOrNull(0)?.type && (item.extraPanel.getOrNull(0)?.extParams?.recommendAdd?:0) >=
                SharePrefUtil.getHotCommentZanThreshold(App.getContext())) {
                item.isHotComment = true
                if (!BlackListManager.INSTANCE.isBlacked(item.reply_id)) {
                    hot.add(item)
                }
            }
        }
        hot.sortWith { o1: PostDetailBean.ListBean, o2: PostDetailBean.ListBean ->
            (o2.extraPanel.getOrNull(0)?.extParams?.recommendAdd?:0) -
                    (o1.extraPanel.getOrNull(0)?.extParams?.recommendAdd?:0)
        }

        return hot
    }

    @JvmStatic
    fun getStickComment(postDetailBean: PostDetailBean): List<PostDetailBean.ListBean> {
        return postDetailBean.list.filter {
            it.poststick == 1 && !BlackListManager.INSTANCE.isBlacked(it.reply_id)
        }
    }

    @JvmStatic
    fun resortComment(postDetailBean: PostDetailBean): List<PostDetailBean.ListBean>? {
        return try {
            val hot = getHotComment(postDetailBean)
            val stick = getStickComment(postDetailBean)

            val hotFilter = hot.filter {
                !stick.contains(it)
            }

            val rest = postDetailBean.list.filter {
                !hot.contains(it) && !stick.contains(it) && !BlackListManager.INSTANCE.isBlacked(it.reply_id)
            }

            val result: MutableList<PostDetailBean.ListBean> = ArrayList(stick)
            result.addAll(hotFilter)
            result.addAll(rest)
            result
        } catch (e: Exception) {
            postDetailBean.list
        }
    }

}