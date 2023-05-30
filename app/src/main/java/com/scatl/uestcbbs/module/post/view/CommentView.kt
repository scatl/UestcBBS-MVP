package com.scatl.uestcbbs.module.post.view

import com.scatl.uestcbbs.base.BaseView
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SupportResultBean

/**
 * Created by sca_tl on 2023/1/13 9:36
 */
interface CommentView: BaseView {
    fun onGetPostCommentSuccess(postDetailBean: PostDetailBean)
    fun onGetPostCommentError(msg: String?, code: Int)
    fun onAppendPost(replyPostsId: Int, tid: Int)
    fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, position: Int)
    fun onSupportError(msg: String?)
    fun onPingFen(pid: Int)
    fun onOnlyReplyAuthor(uid: Int)
    fun onDeletePost(tid: Int, pid: Int)
    fun onStickReplySuccess(msg: String?)
    fun onStickReplyError(msg: String?)
    fun onDianPing(pid: Int)
    fun onGetReplyDataSuccess(postDetailBean: PostDetailBean, replyPosition: Int, replyId: Int)
    fun onGetAwardInfoSuccess(info: String, commentPosition: Int)
}