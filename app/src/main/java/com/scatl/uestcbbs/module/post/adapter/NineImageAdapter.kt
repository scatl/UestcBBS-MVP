package com.scatl.uestcbbs.module.post.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.imageview.ShapeableImageView
import com.scatl.image.ninelayout.NineGridAdapter
import com.scatl.image.ninelayout.NineGridLayout
import com.scatl.uestcbbs.R

/**
 * Created by tanlei02 on 2022/12/8 15:53
 */
class NineImageAdapter(val data: List<String>): NineGridAdapter() {
    override fun getItemView(parent: NineGridLayout, position: Int): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_post_content_image, parent, false)
    }

    override fun bindView(parent: NineGridLayout, view: View, position: Int) {
        val image = view.findViewById<ShapeableImageView>(R.id.image)
        if (data.size == 1) {
            Glide.with(parent.context)
                .asBitmap()
                .load(data[0])
                .placeholder(R.drawable.place_holder)
                .into(object : ImageViewTarget<Bitmap?>(image) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        super.onResourceReady(resource, transition)
                        if (!resource.isRecycled) {
                            parent.resetOneChildWidthAndHeight(resource.width, resource.height)
                        }
                    }

                    override fun setResource(resource: Bitmap?) {
                        image.takeIf {
                            resource != null && !resource.isRecycled
                        }?.setImageBitmap(resource)
                    }
                })
        } else {
            Glide
                .with(parent.context)
                .load(data[position])
                .placeholder(R.drawable.place_holder)
                .dontAnimate()
                .into(image)
        }
    }

    override fun getItemCount() = data.size
}