package com.scatl.uestcbbs.base

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.annotation.IntRange
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.util.SharePrefUtil

/**
 * Created by sca_tl on 2022/10/31 20:05
 */
class BaseAlertDialogBuilder : MaterialAlertDialogBuilder {

    private var mShowOnceId: String? = null
    private var mBlurRadius: Int = 64

    constructor(context: Context) : super(context)

    constructor(context: Context, overrideThemeResId: Int) : super(context, overrideThemeResId)

    override fun create(): AlertDialog {
        return super.create().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.window?.let {
                    it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.attributes.blurBehindRadius = mBlurRadius
                }
            }
        }
    }

    override fun show(): AlertDialog? {
        if (mShowOnceId != null && SharePrefUtil.getShowOnceDialogId(App.getContext()).contains(mShowOnceId)) {
            return null
        }
        mShowOnceId?.let {
            SharePrefUtil.setShowOnceDialogId(App.getContext(), it)
        }
        return super.show()
    }

    fun setBlurRadius(@IntRange(from = 0) radius: Int) = apply {
        mBlurRadius = radius
    }

    fun setShowOnceId(id: String?) = apply {
        mShowOnceId = id
    }
}