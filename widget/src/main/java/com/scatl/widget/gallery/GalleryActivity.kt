package com.scatl.widget.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.SharedElementCallback
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.scatl.util.ColorUtil
import com.scatl.widget.R
import com.scatl.widget.databinding.ActivityGalleryBinding
import com.scatl.widget.iamgeviewer.ImageConstant
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.launch
import java.util.ArrayList

@SuppressLint("SetTextI18n")
internal class GalleryActivity: AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initStatusBar()

        binding.confirmButton.visibility = View.GONE
        binding.slidingLayout.setFadeOnClickListener(this)
        binding.albumName.setOnClickListener(this)
        binding.confirmButton.setOnClickListener(this)

        initRecyclerview()
        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadMedia()
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 10)
        }
    }

    private fun initStatusBar() {
        ImmersionBar
            .with(this)
            .statusBarColorInt(ColorUtil.getAttrColor(this, R.attr.colorSurface))
            .autoStatusBarDarkModeEnable(true)
            .init()
    }

    private fun initRecyclerview() {
        galleryAdapter = GalleryAdapter(this, onMediaClick = { mediaEntity, selected -> onMediaClick(mediaEntity, selected) })
        binding.galleryRv.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            adapter = galleryAdapter
//            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.gallery_item_anim)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val space = 8
                    val column = position % 3
                    outRect.left = column * space / 3
                    outRect.right = space - (column + 1) * space / 3
                    outRect.top = if (position >= 3) space else 0
                }
            })
        }

        albumAdapter = AlbumAdapter(this, onAlbumClick = { onAlbumClick(it) })
        binding.albumRv.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = albumAdapter
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.albumName -> {
                binding.slidingLayout.panelState = PanelState.ANCHORED
            }
            binding.slidingLayout -> {
                binding.slidingLayout.panelState = PanelState.COLLAPSED
            }
            binding.confirmButton -> {
                val intent = Intent().putParcelableArrayListExtra("data", Gallery.INSTANCE.selectedMedia as (ArrayList<out Parcelable>))
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun onMediaClick(mediaEntity: MediaEntity, selected: Boolean) {
        if (Gallery.INSTANCE.selectedMedia.size == 0) {
            binding.confirmButton.visibility = View.GONE
        } else {
            binding.confirmButton.visibility = View.VISIBLE
            binding.confirmButton.text = "完成(${Gallery.INSTANCE.selectedMedia.size})"
        }

        for ((index, value) in albumAdapter.data.withIndex()) {
            if (value.albumPath == mediaEntity.relativePath) {
                if (!selected && value.selectedMedia.contains(mediaEntity)) {
                    value.selectedMedia.remove(mediaEntity)
                } else if (!Gallery.INSTANCE.isReachMaxSelect()){
                    value.selectedMedia.add(mediaEntity)
                }
                albumAdapter.notifyItemChanged(index)
                break
            }
        }
    }

    private fun onAlbumClick(entity: AlbumEntity) {
        binding.slidingLayout.panelState = PanelState.COLLAPSED
        Handler(Looper.getMainLooper()).postDelayed({
            binding.albumName.text = "${entity.albumPath?.dropLast(1)}(${entity.allMedia.size}张)"
            galleryAdapter.data = entity.allMedia
//                binding.galleryRecyclerView.scheduleLayoutAnimation()
        }, 250)
    }

    private fun loadMedia() {
        lifecycleScope.launch {
            val galleryEntity = MediaStoreUtil.queryImages(this@GalleryActivity)
            galleryAdapter.data = galleryEntity.medias
            binding.albumName.text = "全部图片(${galleryEntity.medias.size}张)"
            Gallery.INSTANCE.mSelectedAlbum = ImageConstant.ALL_MEDIA_PATH
            albumAdapter.data = galleryEntity.albums
        }
    }

    override fun onBackPressed() {
        if (binding.slidingLayout.panelState == PanelState.EXPANDED ||
            binding.slidingLayout.panelState == PanelState.ANCHORED) {
            binding.slidingLayout.panelState = PanelState.COLLAPSED
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMedia()
                } else {
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (showRationale) {
                        finish()
                    } else {
                        goToSettings()
                        finish()
                    }
                }
            }
        }
    }

    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

}