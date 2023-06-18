package com.scatl.widget.gallery

/**
 * Created by sca_tl at 2023/6/15 10:35
 */
enum class MimeType(val type: String) {
    JPEG(type = "image/jpeg"),
    PNG(type = "image/png"),
    WEBP(type = "image/webp"),
    HEIC(type = "image/heic"),
    HEIF(type = "image/heif"),
    BMP(type = "image/x-ms-bmp"),
    GIF(type = "image/gif"),
    MPEG(type = "video/mpeg"),
    MP4(type = "video/mp4"),
    QUICKTIME(type = "video/quicktime"),
    THREEGPP(type = "video/3gpp"),
    THREEGPP2(type = "video/3gpp2"),
    MKV(type = "video/x-matroska"),
    WEBM(type = "video/webm"),
    TS(type = "video/mp2ts"),
    AVI(type = "video/avi");

    companion object {

        fun ofAll(): List<MimeType> {
            return values().toList()
        }

        fun ofImage(exclude: List<MimeType>): List<MimeType> {
            return values().filter { !exclude.contains(it) }
        }

        fun ofGif(): List<MimeType> {
            return mutableListOf(GIF)
        }

        fun ofImage(hasGif: Boolean = true): List<MimeType> {
            return if (hasGif) {
                values().filter { it.isImage }
            } else {
                values().filter { it.isImage && it != GIF }
            }
        }

        fun ofVideo(): List<MimeType> {
            return values().filter { it.isVideo }
        }

    }

}