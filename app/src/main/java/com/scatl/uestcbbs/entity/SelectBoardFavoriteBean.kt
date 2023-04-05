package com.scatl.uestcbbs.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * Created by sca_tl at 2023/4/4 13:33
 */
data class SelectBoardFavoriteBean (
    @Column(index = true)
    var id: Int = 0,

    var uid: Int? = 0,
    var boardId: Int? = -1,
    var boardName: String? = "",
    var classificationId: Int? = -1,
    var classificationName: String? = "",
): LitePalSupport(), Serializable