package com.scatl.widget.gallery

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object MediaStoreUtil {

    @JvmStatic
    fun queryImages(context: Context): GalleryEntity {
        val images = mutableListOf<MediaEntity>()
        val albums = mutableListOf<AlbumEntity>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val albumName = mutableSetOf<String>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, sortOrder)?.use { cursor ->

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val absolutePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                val image = MediaEntity(id, absolutePath, relativePath, displayName, dateModified, uri, bucketName)

                images += image
                albumName += image.relativePath
            }
        }

        albums.add(AlbumEntity(Gallery.ALL_MEDIA_PATH, Gallery.ALL_MEDIA_PATH, images[0].uri, images, mutableListOf()))

        albumName.forEach {
            val albumImages = images.filter { mediaEntity ->
                mediaEntity.relativePath == it
            }
            albums.add(AlbumEntity(albumImages[0].albumName, it, albumImages[0].uri,
                albumImages as MutableList<MediaEntity>, mutableListOf()))
        }

        return GalleryEntity(albums, images)
    }

}