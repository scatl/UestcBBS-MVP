package com.scatl.uestcbbs.entity

/**
 * created by sca_tl at 2023/5/2 15:17
 */
data class ViewWarningEntity(
    var dsp: String? = "",
    var items: MutableList<ViewWarningItem>? = mutableListOf()
)

data class ViewWarningItem(
    var name: String? = "",
    var uid: Int? = 0,
    var avatar: String? = "",
    var time: String? = "",
    var reason: String? = ""
)