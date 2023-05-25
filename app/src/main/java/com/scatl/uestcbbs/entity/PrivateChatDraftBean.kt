package com.scatl.uestcbbs.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class PrivateChatDraftBean (
    @Column(index = true)
    var id: Int = 0,

    var hostUid: Int? = 0,
    var chatUid: Int? = 0,
    var content: String? = "",
): LitePalSupport(), Serializable
