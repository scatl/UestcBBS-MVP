package com.scatl.uestcbbs.module.post.adapter

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.ImageUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.gallery.MediaEntity
import com.scatl.widget.iamgeviewer.ImageViewer
import com.scatl.widget.ninelayout.NineGridAdapter
import com.scatl.widget.ninelayout.NineGridLayout
import kotlin.math.min

/**
 * Created by sca_tl on 2022/12/8 15:53
 */
class NineImageAdapter(val data: List<String>): NineGridAdapter() {

    override fun getItemView(parent: NineGridLayout, position: Int): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_post_content_image, parent, false)
    }

    override fun bindView(parent: NineGridLayout, view: View, position: Int) {
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        val textview = view.findViewById<TextView>(R.id.text)
        if (parent.context == null || ((parent.context is Activity) && (parent.context as Activity).isFinishing)) {
            return
        }
        if (data.size == 1) {
            Glide
                .with(parent.context)
                .asDrawable()
                .load(data[0])
                .placeholder(R.drawable.place_holder)
                .into(object : ImageViewTarget<Drawable?>(image) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                        super.onResourceReady(resource, transition)

                        if (resource is AnimationDrawable) {
                            resource.start()
                        }

                        val w = resource.intrinsicWidth
                        val h = resource.intrinsicHeight

                        var reW = w
                        var reH = h

                        val ratio = w.toFloat() / h.toFloat()
                        if (ratio <= 0.2) {
                            if (w < parent.width * 0.5) {
                                reW = (parent.width * 0.5).toInt()
                                reH = ((parent.width * 0.5 / w) * h).toInt()
                            }
                            if (reH >= ScreenUtil.dip2pxF(parent.context, 400f)) {
                                reH = ScreenUtil.dip2pxF(parent.context, 400f).toInt()
                            }
                        } else {
                            if (w < parent.width * 0.67) {
                                reW = (parent.width * 0.67).toInt()
                                reH = ((parent.width * 0.67 / w) * h).toInt()
                            }
                            if (reH >= ScreenUtil.dip2pxF(parent.context, 300f)) {
                                reH = ScreenUtil.dip2pxF(parent.context, 300f).toInt()
                            }
                        }

                        reW = min(reW, parent.width)

                        image.layoutParams = image.layoutParams.apply {
                            width = reW
                            height = reH
                        }
                    }

                    override fun setResource(resource: Drawable?) {
                        image.setImageDrawable(resource)
                    }

                })
        } else {
            Glide
                .with(parent.context)
                .asDrawable()
                .load(data[position])
                .placeholder(R.drawable.place_holder)
                .into(object : ImageViewTarget<Drawable?>(image) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                        super.onResourceReady(resource, transition)
                        if (resource is AnimationDrawable) {
                            resource.start()
                        }
                    }

                    override fun setResource(resource: Drawable?) {
                        image.setImageDrawable(resource)
                    }
                })
            textview?.apply {
                if (data.size > 9 && position == 8) {
                    visibility = View.VISIBLE
                    text = "+".plus(data.size - 9)
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount() = data.size

    override fun onItemClick(view: View, position: Int) {
        val entities = mutableListOf<MediaEntity>()
        for (i in data.indices) {
            val entity = MediaEntity().apply {
                isNet = true
                uri = Uri.parse(data[i])
            }
            entities.add(entity)
        }

//        ImageViewer
//            .INSTANCE
//            .with(view.context)
//            .setEnterView(view.findViewById(R.id.image))
//            .setEnterIndex(position)
//            .setMediaEntity(entities)
//            .setSavePath("uestcbbs")
//            .setViewChangeListener { p ->
//                (view.parent as? ViewGroup)?.getChildAt(p)?.findViewById(R.id.image)
//            }
//            .show()

        ImageUtil.showImages(view.context, data, position)
    }
}