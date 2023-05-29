package com.scatl.widget.gallery

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.scatl.widget.R
import com.scatl.widget.iamgeviewer.ImageViewer
import com.scatl.widget.load

//import com.scatl.widget.viewer.ImageViewer

/**
 * Created by sca_tl on 2022/8/12 13:59
 */
@SuppressLint("NotifyDataSetChanged")
internal class GalleryAdapter(private val mContext: Context, val onMediaClick: (MediaEntity, selected: Boolean) -> Unit):
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private var mRecyclerView: RecyclerView? = null

    companion object {
        const val IMAGE_MAX_SCALE = 1.3f
    }

    var data: List<MediaEntity> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val isSelected = Gallery.INSTANCE.selectedMedia.contains(data[position])

        holder.shadow.apply {
            alpha = if (isSelected) 1f else 0f
        }

        holder.dsp.apply {
            if (data[position].isGif) {
                visibility = View.VISIBLE
                text = "GIF"
            } else if (data[position].isHeic) {
                visibility = View.VISIBLE
                text = "HEIC"
            } else if (data[position].isWebp) {
                visibility = View.VISIBLE
                text = "WEBP"
            } else {
                visibility = View.GONE
            }
        }

        holder.image.apply {
            load(data[position].uri)
            if (isSelected) {
                scaleX = IMAGE_MAX_SCALE
                scaleY = IMAGE_MAX_SCALE
            } else {
                scaleX = 1.0f
                scaleY = 1.0f
            }
        }

        holder.checkBox.apply {
            setOnCheckedChangeListener(null)
            isChecked = isSelected
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (Gallery.INSTANCE.isReachMaxSelect()) {
                        this.isChecked = false
                        Toast.makeText(context, "最多选择${Gallery.INSTANCE.maxSelect}张图片", Toast.LENGTH_SHORT).show()
                    } else {
                        Gallery.INSTANCE.selectedMedia.add(data[position])
                        scaleImage(holder, 1.0f, IMAGE_MAX_SCALE)
                        alphaShadow(holder, 0f, 1f)
                    }
                } else {
                    Gallery.INSTANCE.selectedMedia.remove(data[position])
                    scaleImage(holder, IMAGE_MAX_SCALE, 1.0f)
                    alphaShadow(holder, 1f, 0f)
                }
                onMediaClick(data[position], isChecked)
            }
        }

        holder.itemView.setOnClickListener {
            ImageViewer
                .INSTANCE
                .with(mContext as AppCompatActivity)
                .setMediaEntity(data)
                .setEnterIndex(position)
                .setEnterView(holder.image)
                .setViewChangeListener { currentPosition ->
                    (mRecyclerView?.layoutManager as? LinearLayoutManager?)
                        ?.findViewByPosition(currentPosition)
                        ?.findViewById(R.id.image)
                }
                .show()
        }
    }

    private fun scaleImage(holder: GalleryViewHolder, originScale: Float, finalScale: Float) {
        if (holder.image.scaleX == originScale) {
            ValueAnimator
                .ofFloat(originScale, finalScale)
                .setDuration(300)
                .apply {
                    addUpdateListener { animator ->
                        holder.image.scaleX = animator.animatedValue as Float
                        holder.image.scaleY = animator.animatedValue as Float
                    }
                    start()
                }
        }
    }

    private fun alphaShadow(holder: GalleryViewHolder, from: Float, to: Float) {
        if (holder.shadow.alpha == from) {
            ValueAnimator
                .ofFloat(from, to)
                .setDuration(300)
                .apply {
                    addUpdateListener { animator ->
                        holder.shadow.alpha = animator.animatedValue as Float
                    }
                    start()
                }
        }
    }

    override fun getItemCount() = data.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    inner class GalleryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val checkBox: MaterialCheckBox = view.findViewById(R.id.checkbox)
        val dsp: TextView = view.findViewById(R.id.dsp)
        val shadow: View = view.findViewById(R.id.shadow)
    }

}

