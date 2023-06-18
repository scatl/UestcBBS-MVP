package com.scatl.widget.gallery

import android.database.Cursor

/**
 * Created by sca_tl at 2023/6/14 20:18
 */

internal val MimeType.isImage: Boolean
    get() = type.startsWith(prefix = "image/")

internal val MimeType.isVideo: Boolean
    get() = type.startsWith(prefix = "video/")

fun Cursor.getInt(columnName: String, default: Int): Int {
    return try {
        val columnIndex = getColumnIndexOrThrow(columnName)
        getInt(columnIndex)
    } catch (e: Throwable) {
        e.printStackTrace()
        default
    }
}

fun Cursor.getString(columnName: String, default: String): String {
    return try {
        val columnIndex = getColumnIndexOrThrow(columnName)
        getString(columnIndex) ?: default
    } catch (e: Throwable) {
        e.printStackTrace()
        default
    }
}

fun Cursor.getLong(columnName: String, default: Long): Long {
    return try {
        val columnIndex = getColumnIndexOrThrow(columnName)
        getLong(columnIndex)
    } catch (e: Throwable) {
        e.printStackTrace()
        default
    }
}