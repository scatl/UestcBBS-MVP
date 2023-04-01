package com.scatl.uestcbbs.entity

data class SelectBoardResultEvent(
    var boardCategoryId: Int? = -1,
    var boardCategoryName: String? = "",
    var fatherBoardId: Int? = -1,
    var fatherBName: String? = "",
    var childBoardId: Int? = -1,
    var childBoardName: String? = "",
    var classificationId: Int? = -1,
    var classificationName: String? = "",
)
