package com.scatl.widget.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.gyf.immersionbar.ImmersionBar
import com.scatl.widget.R
import com.scatl.widget.databinding.ActivityGalleryBinding
import com.scatl.util.ColorUtil
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.launch

class GalleryActivity: AppCompatActivity() {

    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ImmersionBar.with(this)
            .statusBarColor(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurface))
            .autoStatusBarDarkModeEnable(true)
            .init()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.confirm -> {
                    Gallery.INSTANCE.mOnMediaSelectedListener?.get()?.onConfirm(
                        Gallery.INSTANCE.selectedMedia
                    )
                    finish()
                    true
                }
                else -> false
            }
        }

        binding.slidingLayout.apply {
            setFadeOnClickListener {
                panelState = PanelState.COLLAPSED
            }
        }
        binding.albumName.setOnClickListener {
            binding.slidingLayout.panelState = PanelState.ANCHORED
        }

        galleryAdapter = GalleryAdapter(this, onMediaClick = { mediaEntity, selected ->
            binding.toolbar.menu.findItem(R.id.confirm).title = "完成(${Gallery.INSTANCE.selectedMedia.size})"
            for ((index, value) in albumAdapter.data.withIndex()) {
                if (value.albumPath == mediaEntity.relativePath) {
                    if (!selected && value.selectedMedia.contains(mediaEntity)) {
                        value.selectedMedia.remove(mediaEntity)
                    } else if (!Gallery.INSTANCE.isReachMax()){
                        value.selectedMedia.add(mediaEntity)
                    }
                    albumAdapter.notifyItemChanged(index)
                    break
                }
            }
        })
        binding.galleryRecyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            adapter = galleryAdapter
        }

        albumAdapter = AlbumAdapter(this, onAlbumClick = {
            binding.slidingLayout.panelState = PanelState.COLLAPSED
            Handler(Looper.getMainLooper()).postDelayed({
                binding.albumName.text = "${it.albumPath.dropLast(1)}(${it.allMedia.size}张)"
                galleryAdapter.data = it.allMedia
            }, 250)
        })
        binding.albumRecyclerView.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(this@GalleryActivity)
            adapter = albumAdapter
        }

        loadMedia()
    }

    private fun loadMedia() {
        lifecycleScope.launch {
            val galleryEntity = MediaStoreUtil.queryImages(this@GalleryActivity)
            galleryAdapter.data = galleryEntity.medias
            binding.albumName.text = "全部图片(${galleryEntity.medias.size}张)"
            Gallery.INSTANCE.mSelectedAlbum = Gallery.ALL_MEDIA_PATH
            albumAdapter.data = galleryEntity.albums
        }
    }

    override fun onBackPressed() {
        if (binding.slidingLayout.panelState == PanelState.EXPANDED ||
            binding.slidingLayout.panelState == PanelState.ANCHORED) {
            binding.slidingLayout.panelState = PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

}