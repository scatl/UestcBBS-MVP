package com.scatl.widget.gallery

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object MediaStoreUtil {

    enum class Projection(val value: String) {
        ID(MediaStore.MediaColumns._ID),
        DISPLAY_NAME(MediaStore.MediaColumns.DISPLAY_NAME),
        DATA(MediaStore.MediaColumns.DATA),
        DATE_MODIFIED(MediaStore.MediaColumns.DATE_MODIFIED),
        BUCKET_DISPLAY_NAME(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME),
        WIDTH(MediaStore.MediaColumns.WIDTH),
        HEIGHT(MediaStore.MediaColumns.HEIGHT),
        MIME_TYPE(MediaStore.MediaColumns.MIME_TYPE),
        BUCKET_ID(MediaStore.MediaColumns.BUCKET_ID),
        RELATIVE_PATH(MediaStore.MediaColumns.RELATIVE_PATH);

        companion object {
            fun arrayProjection(): Array<String> {
                val projection = Array(values().size) { "" }
                values().forEachIndexed { i, v ->
                    projection[i] = v.value
                }
                return projection
            }
        }
    }

    @JvmStatic
    private fun buildMediaEntity(cursor: Cursor, contentUri: Uri): MediaEntity {
        val id = cursor.getLong(Projection.ID.value, Long.MAX_VALUE)
        val absolutePath = cursor.getString(Projection.DATA.value, "")
        val relativePath = cursor.getString(Projection.RELATIVE_PATH.value, "未知路径/")
        val dateModified = cursor.getLong(Projection.DATE_MODIFIED.value, Long.MAX_VALUE)
        val displayName = cursor.getString(Projection.DISPLAY_NAME.value, "")
        val uri = ContentUris.withAppendedId(contentUri, id)
        val bucketName = cursor.getString(Projection.BUCKET_DISPLAY_NAME.value, "")
        val bucketId = cursor.getInt(Projection.BUCKET_ID.value, Int.MAX_VALUE)
        val width = cursor.getInt(Projection.WIDTH.value, Int.MAX_VALUE)
        val height = cursor.getInt(Projection.HEIGHT.value, Int.MAX_VALUE)
        val mimeType = cursor.getString(Projection.MIME_TYPE.value, "")

        return MediaEntity(
            id = id,
            absolutePath = absolutePath,
            relativePath = relativePath,
            name = displayName,
            modifyDate = dateModified,
            uri = uri,
            albumName = bucketName,
            bucketId = bucketId,
            width = width,
            height = height,
            mimeType = mimeType,
            isGif = mimeType == MimeType.GIF.type,
            isHeic = mimeType == MimeType.HEIC.type,
            isWebp = mimeType == MimeType.WEBP.type,
            isVideo = mimeType.startsWith(prefix = "video/")
        )
    }

    @JvmStatic
    fun queryMedias(context: Context, config: Gallery): GalleryEntity {
        val images = arrayListOf<MediaEntity>()
        val albums = arrayListOf<AlbumEntity>()

        val sortOrder = "${Projection.DATE_MODIFIED.value} DESC"
        val contentUri = MediaStore.Files.getContentUri("external")

        //按照指定的mimeType过滤
        val selection = StringBuilder()
        selection.append(MediaStore.MediaColumns.MIME_TYPE)
        selection.append(" IN (")
        config.mimeTypes.forEachIndexed { index, mimeType ->
            if (index != 0) {
                selection.append(",")
            }
            selection.append("'${mimeType.type}'")
        }
        selection.append(")")

        val bucketIds = mutableSetOf<Int>()

        context
            .contentResolver
            .query(contentUri, Projection.arrayProjection(), selection.toString(), null, sortOrder)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val image = buildMediaEntity(cursor, contentUri)
                    images.add(image)
                    bucketIds.add(image.bucketId)
                }
            }

        albums.add(
            AlbumEntity(
                albumName = GalleryConstant.ALL_MEDIA_PATH,
                albumRelativePath = GalleryConstant.ALL_MEDIA_PATH,
                albumAbsolutePath =  GalleryConstant.ALL_MEDIA_PATH,
                albumId = GalleryConstant.ALL_MEDIA_BUCKET_ID,
                allMedia = images,
                selectedMedia = arrayListOf()
            )
        )

        bucketIds.forEach {
            val albumImages = images.filter { mediaEntity ->
                mediaEntity.bucketId == it
            }
            albums.add(
                AlbumEntity(
                    albumName = albumImages[0].albumName,
                    albumRelativePath = albumImages[0].relativePath,
                    albumAbsolutePath = albumImages[0].absolutePath,
                    albumId = it,
                    allMedia = albumImages as ArrayList<MediaEntity>,
                    arrayListOf()
                )
            )
        }

        return GalleryEntity(albums, images)
    }

    @JvmStatic
    fun queryMediaByUri(context: Context, uri: Uri?): MediaEntity? {
        if (uri == null) {
            return null
        }
        val images = arrayListOf<MediaEntity>()

        val sortOrder = "${Projection.DATE_MODIFIED.value} DESC"
        val contentUri = MediaStore.Files.getContentUri("external")
        val selection = Projection.ID.value + " = " + ContentUris.parseId(uri)

        context
            .contentResolver
            .query(contentUri, Projection.arrayProjection(), selection, null, sortOrder)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val image = buildMediaEntity(cursor, contentUri)
                    images.add(image)
                }
            }
        return if (images.isEmpty()) {
            null
        } else {
            images[0]
        }
    }

    @JvmStatic
    fun createImage(context: Context, fileName: String): Uri? {
        return try {
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val newImage = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            }
            context.contentResolver.insert(imageCollection, newImage)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}