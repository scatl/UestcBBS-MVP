package com.scatl.uestcbbs.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.scatl.uestcbbs.R

/**
 * Created by sca_tl on 2022/12/7 10:12
 */
class ContentImageGetter(val context: Context, val textView: TextView): Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable {
        val placeHolder = DrawablePlaceHolder()
        Glide
            .with(context)
            .load(source)
            .into(ImageGetterTarget(textView, placeHolder))
        return placeHolder
    }

    class ImageGetterTarget(view: TextView,
                            val placeholder: DrawablePlaceHolder) : CustomViewTarget<TextView, Drawable>(view) {

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            resource.setBounds(0, 0, 80, 80)
            placeholder.setBounds(0, 0, 80, 80)
            placeholder.drawable = resource
            view.text = view.text
            if (resource is Animatable) {
                (resource as Animatable).start()
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) { }

        override fun onResourceCleared(placeholder: Drawable?) { }

    }

    class DrawablePlaceHolder : BitmapDrawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }
}