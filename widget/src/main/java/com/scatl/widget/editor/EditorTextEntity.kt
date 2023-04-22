package com.scatl.widget.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class EditorTextEntity(
    var content: String = "",
    override val type: Int = EntityType.TYPE_TEXT,
    var requestFocus: Boolean = false,
    var hint: String = "在这里输入内容"
): BaseEditorEntity(), Serializable, Parcelable
