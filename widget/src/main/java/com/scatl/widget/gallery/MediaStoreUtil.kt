package com.scatl.widget.gallery

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.scatl.widget.iamgeviewer.ImageConstant

object MediaStoreUtil {

    @JvmStatic
    fun queryImages(context: Context): GalleryEntity {
        val images = arrayListOf<MediaEntity>()
        val albums = arrayListOf<AlbumEntity>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        var selection: String? = null

        if (!Gallery.INSTANCE.mShowGif) {
            selection = MediaStore.Images.Media.MIME_TYPE + "!='image/gif'"
        }

        val albumName = mutableSetOf<String>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null, sortOrder)?.use { cursor ->

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val absolutePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
                val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))

                val image = MediaEntity(id, absolutePath, relativePath, displayName, dateModified, uri, bucketName, mimeType, width, height)

                when(mimeType) {
                    "image/gif" -> image.isGif = true
                    "image/heic" -> image.isHeic = true
                    "image/webp" -> image.isWebp = true
                }

                images.add(image)
                albumName.add(image.relativePath ?: "空路径")
            }
        }

        albums.add(AlbumEntity(ImageConstant.ALL_MEDIA_PATH, ImageConstant.ALL_MEDIA_PATH, images[0].uri, images, arrayListOf()))

        albumName.forEach {
            val albumImages = images.filter { mediaEntity ->
                mediaEntity.relativePath == it
            }
            albums.add(AlbumEntity(albumImages[0].albumName, it, albumImages[0].uri,
                albumImages as ArrayList<MediaEntity>, arrayListOf()
            ))
        }

        return GalleryEntity(albums, images)
    }

}