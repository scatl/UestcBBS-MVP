package com.scatl.uestcbbs.widget.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.scatl.uestcbbs.R

/**
 * Created by sca_tl on 2022/12/29 20:19
 */
class EmojiImageGetter(val context: Context,
                       val textView: TextView): Html.ImageGetter, Drawable.Callback {

    init {
        textView.setTag(R.id.drawable_tag, this)
    }

    override fun getDrawable(source: String?): Drawable {
        val emojiDrawable = EmojiDrawable()
        Glide
            .with(context)
            .load(source)
            .into(ImageGetterTarget(textView, emojiDrawable))
        return emojiDrawable
    }

    override fun invalidateDrawable(who: Drawable) {
        textView.invalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {

    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {

    }

    private class ImageGetterTarget(val textView: TextView,
                                    val mEmojiDrawable: EmojiDrawable
    ) : ViewTarget<TextView, Drawable>(textView) {

        private var request: Request? = null

        override fun setRequest(request: Request?) {
            this.request = request
        }

        override fun getRequest() = request

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            val radio = resource.intrinsicWidth.toFloat() / resource.intrinsicHeight.toFloat()
            val rect = Rect(0, 0, (textView.textSize * radio * 1.5f).toInt(), (textView.textSize * 1.5f).toInt())
            resource.bounds = rect
            mEmojiDrawable.bounds = rect

            mEmojiDrawable.setDrawable(resource)
            if (resource is Animatable) {
                mEmojiDrawable.callback = getView().getTag(R.id.drawable_tag) as? EmojiImageGetter
                (resource as Animatable).start()
            }
            getView().text = getView().text
            getView().invalidate()
        }
    }

    class EmojiDrawable: Drawable(), Drawable.Callback {

        private var mDrawable: Drawable? = null

        fun setDrawable(drawable: Drawable) {
            mDrawable?.callback = null
            drawable.callback = this
            this.mDrawable = drawable
        }

        override fun draw(canvas: Canvas) {
            mDrawable?.draw(canvas)
        }

        override fun setAlpha(alpha: Int) {
            mDrawable?.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            mDrawable?.colorFilter = colorFilter
        }

        override fun getOpacity() = mDrawable?.opacity ?: PixelFormat.UNKNOWN

        override fun invalidateDrawable(who: Drawable) {
            callback?.invalidateDrawable(who)
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            callback?.scheduleDrawable(who, what, `when`)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            callback?.unscheduleDrawable(who, what)
        }
    }

}