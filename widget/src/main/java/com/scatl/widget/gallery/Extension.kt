package com.scatl.widget.gallery

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide

/**
 * Created by sca_tl at 2023/4/20 11:44
 */
fun ImageView.load(url: String) {
    Glide.with(context).load(url).into(this)
}

fun ImageView.load(@RawRes @DrawableRes resId: Int) {
    Glide.with(context).load(resId).into(this)
}

fun ImageView.load(uri: Uri) {
    Glide.with(context).load(uri).into(this)
}