package com.scatl.widget.editor

import java.io.Serializable

/**
 * created by sca_tl at 2022/6/26 21:27
 */
abstract class BaseEditorEntity: Serializable {
    @Transient
    open val type: Int = 0
}

object EntityType {
    const val TYPE_TEXT = 0
    const val TYPE_IMAGE = 1
}
