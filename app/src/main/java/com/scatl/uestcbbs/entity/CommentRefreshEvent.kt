package com.scatl.uestcbbs.entity

/**
 * Created by sca_tl at 2023/4/17 16:21
 */
data class CommentRefreshEvent(
    var topicId: Int? = 0,
    var commentNum: Int? = 0
)