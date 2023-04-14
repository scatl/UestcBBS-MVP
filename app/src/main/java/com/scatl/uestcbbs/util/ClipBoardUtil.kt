package com.scatl.uestcbbs.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.annotation.ToastType

object ClipBoardUtil {

    @JvmStatic
    fun copyToClipBoard(context: Context?, text: String?) {
        if (context == null) {
            ToastUtil.showToast(App.getContext(), "复制失败", ToastType.TYPE_ERROR)
            return
        }
        if (text == null) {
            ToastUtil.showToast(App.getContext(), "内容为空", ToastType.TYPE_ERROR)
            return
        }

        (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?)?.let { manager ->
            manager.setPrimaryClip(ClipData.newPlainText("1", text))
            manager.primaryClip?.let {
                if (it.getItemAt(0).text.toString() == text) {
                    ToastUtil.showToast(context, "复制成功", ToastType.TYPE_SUCCESS)
                } else {
                    ToastUtil.showToast(context, "复制失败，请检查是否拥有剪切板权限", ToastType.TYPE_SUCCESS)
                }
                return
            }
            return
        }
        ToastUtil.showToast(context, "复制失败，请检查是否拥有剪切板权限", ToastType.TYPE_SUCCESS)
    }

}