package com.scatl.widget.download

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import java.net.URLDecoder

/**
 * Created by sca_tl at 2023/2/28 14:52
 */
object DownLoadUtil {

    @JvmStatic
    fun isDownloadFolderUriAccessible(context: Context): Boolean {
        for (persistedUriPermission in context.contentResolver.persistedUriPermissions) {
            if (persistedUriPermission.uri.toString() == getDownloadFolderUri(context)) {
                return true
            }
        }
        setDownloadFolderUri(context, "")
        return false
    }

    @JvmStatic
    fun setDownloadFolderUri(context: Context, uriString: String) {
        val sharedPreferences = context.getSharedPreferences("download", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("download_folder_uri", uriString)
        editor.apply()
    }

    @JvmStatic
     fun getDownloadFolderUri(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("download", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("download_folder_uri","")
    }

    @JvmStatic
    fun getDownloadFolder(context: Context) =
        URLDecoder
            .decode(getDownloadFolderUri(context), "UTF-8")
            .replace("content://com.android.externalstorage.documents/tree/primary:", "/storage/emulated/0/")
            .plus("/")

    @JvmStatic
    fun isFileExist(context: Context, name: String?): Boolean {
        DocumentFile
            .fromTreeUri(context, Uri.parse(getDownloadFolderUri(context)))
            ?.listFiles()
            ?.find { it.name == name }
            ?.let { return true }
            ?: return false
    }

    @JvmStatic
    fun getExistFile(context: Context, name: String?): DocumentFile? {
        DocumentFile
            .fromTreeUri(context, Uri.parse(getDownloadFolderUri(context)))
            ?.listFiles()
            ?.find { it.name == name }
            ?.let { return it }
            ?: return null
    }

}