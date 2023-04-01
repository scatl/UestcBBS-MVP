package com.scatl.uestcbbs.module.message

/**
 * Created by sca_tl at 2023/3/16 15:42
 */
class MessageManager {

    var pmUnreadCount = 0
    var atUnreadCount = 0
    var replyUnreadCount = 0
    var systemUnreadCount = 0
    var dianPingUnreadCount = 0

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MessageManager() }
    }

    fun getUnreadMsgCount() = pmUnreadCount + atUnreadCount + replyUnreadCount +
            systemUnreadCount + dianPingUnreadCount

    fun decreasePmCount() {
        if (pmUnreadCount > 1) {
            pmUnreadCount -= 1
        } else {
            pmUnreadCount = 0
        }
    }

    fun resetCount() {
        pmUnreadCount = 0
        atUnreadCount = 0
        replyUnreadCount = 0
        systemUnreadCount = 0
        dianPingUnreadCount = 0
    }

}