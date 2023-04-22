package com.scatl.widget.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class EditorImageEntity(
    var path: String = "",
    override val type: Int = EntityType.TYPE_IMAGE,
    var width: Int = 0,
    var height: Int = 0,
    var dsp: String = "",
    var dspHint: String = "添加图片描述"
): BaseEditorEntity(), Serializable, Parcelable
