package com.scatl.uestcbbs.module.post.adapter

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.image.ninelayout.NineGridAdapter
import com.scatl.image.ninelayout.NineGridLayout
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.ImageUtil

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
        if (parent.context == null ||
            ((parent.context is Activity) && (parent.context as Activity).isFinishing)) {
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
                        parent.resetOneChildSize(resource.intrinsicWidth, resource.intrinsicHeight)
//                        if (!resource.isRecycled) {
//                            parent.resetOneChildWidthAndHeight(resource.width, resource.height)
//                        }
                    }

                    override fun setResource(resource: Drawable?) {
                        image.setImageDrawable(resource)
//                        image.takeIf {
//                            resource != null && !resource.isRecycled
//                        }?.setImageBitmap(resource)
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
                        parent.resetOneChildSize(resource.intrinsicWidth, resource.intrinsicHeight)
//                        if (!resource.isRecycled) {
//                            parent.resetOneChildWidthAndHeight(resource.width, resource.height)
//                        }
                    }

                    override fun setResource(resource: Drawable?) {
                        image.setImageDrawable(resource)
//                        image.takeIf {
//                            resource != null && !resource.isRecycled
//                        }?.setImageBitmap(resource)
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
        ImageUtil.showImages(view.context, data, position)
    }
}