package com.scatl.widget.gallery

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.checkbox.MaterialCheckBox
import com.scatl.widget.R
import com.scatl.widget.iamgeviewer.ImageViewer

/**
 * Created by sca_tl on 2022/8/12 13:59
 */
@SuppressLint("NotifyDataSetChanged")
internal class GalleryAdapter(private val mContext: Context,
                              val config: Gallery,
                              val onMediaClick: (MediaEntity, selected: Boolean) -> Unit,
                              val onCameraClick: () -> Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isAllMediaAlbum = false
    var selectedMedia = mutableListOf<MediaEntity>()
    private var mRecyclerView: RecyclerView? = null

    companion object {
        const val IMAGE_MAX_SCALE = 1.2f

        const val VIEW_TYPE_CAMERA = 0
        const val VIEW_TYPE_IMAGE = 1
    }

    var data: MutableList<MediaEntity> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_CAMERA) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item_camera, parent, false)
            return CameraViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item_image, parent, false)
            return GalleryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            VIEW_TYPE_CAMERA -> {
                bindCamera(holder as CameraViewHolder, position)
            }
            VIEW_TYPE_IMAGE -> {
                bindGalleryImage(holder as GalleryViewHolder, position)
            }
        }
    }

    private fun bindGalleryImage(holder: GalleryViewHolder, position: Int) {
        val realMediaPosition = if (isAllMediaAlbum) position - 1 else position
        val mediaEntity = data[realMediaPosition]
        val isSelected = selectedMedia.contains(mediaEntity)

        holder.shadow.apply {
            alpha = if (isSelected) 1f else 0.1f
        }

        holder.videoIcon.apply {
            visibility = if (mediaEntity.isVideo) View.VISIBLE else View.GONE
        }

        holder.dsp.apply {
            if (mediaEntity.isGif) {
                visibility = View.VISIBLE
                text = "GIF"
            } else if (mediaEntity.isHeic) {
                visibility = View.VISIBLE
                text = "HEIC"
            } else if (mediaEntity.isWebp) {
                visibility = View.VISIBLE
                text = "WEBP"
            } else {
                visibility = View.GONE
            }
        }

        holder.image.apply {
            Glide
                .with(context)
                .load(mediaEntity.uri)
                .transition(DrawableTransitionOptions().crossFade())
                .dontAnimate()
                .into(this)
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
                    if (selectedMedia.size == config.maxSelect) {
                        this.isChecked = false
                        Toast.makeText(context, "最多选择${config.maxSelect}张图片", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedMedia.add(mediaEntity)
                        scaleImage(holder, 1.0f, IMAGE_MAX_SCALE)
                        alphaShadow(holder, 0.1f, 1f)
                        onMediaClick(mediaEntity, true)
                    }
                } else {
                    selectedMedia.remove(mediaEntity)
                    //scaleImage(holder, IMAGE_MAX_SCALE, 1.0f)
                    alphaShadow(holder, 1f, 0.1f)
                    onMediaClick(mediaEntity, false)
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (mediaEntity.isVideo) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(mediaEntity.uri, "video/*")
                    mContext.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(mContext, "没有可用于播放视频的应用", Toast.LENGTH_SHORT).show()
                }
            } else {
                ImageViewer
                    .INSTANCE
                    .with(mContext as AppCompatActivity)
                    .setMediaEntity(data)
                    .setEnterIndex(realMediaPosition)
                    .setEnterView(holder.image)
                    .setViewChangeListener { currentPosition ->
                        (mRecyclerView?.layoutManager as? LinearLayoutManager?)
                            ?.findViewByPosition(if (isAllMediaAlbum) currentPosition + 1 else currentPosition)
                            ?.findViewById(R.id.image)
                    }
                    .show()
            }
        }
    }

    private fun bindCamera(holder: CameraViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onCameraClick.invoke()
        }
    }

    private fun scaleImage(holder: GalleryViewHolder, originScale: Float, finalScale: Float) {
        ValueAnimator
            .ofFloat(originScale, finalScale, originScale)
            .setDuration(300)
            .apply {
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animator ->
                    holder.image.scaleX = animator.animatedValue as Float
                    holder.image.scaleY = animator.animatedValue as Float
                }
                start()
            }
    }

    private fun alphaShadow(holder: GalleryViewHolder, from: Float, to: Float) {
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

    override fun getItemCount() = data.size + if (isAllMediaAlbum) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && isAllMediaAlbum) {
            VIEW_TYPE_CAMERA
        } else {
            VIEW_TYPE_IMAGE
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    inner class GalleryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val checkBox: MaterialCheckBox = view.findViewById(R.id.checkbox)
        val dsp: TextView = view.findViewById(R.id.dsp)
        val shadow: View = view.findViewById(R.id.shadow)
        val videoIcon: ImageView = view.findViewById(R.id.video_icon)
    }

    inner class CameraViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }

}

