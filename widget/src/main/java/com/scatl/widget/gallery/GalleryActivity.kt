package com.scatl.widget.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.gyf.immersionbar.ImmersionBar
import com.scatl.util.ColorUtil
import com.scatl.util.PermissionUtils
import com.scatl.widget.R
import com.scatl.widget.databinding.ActivityGalleryBinding
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetTextI18n")
internal class GalleryActivity: AppCompatActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityGalleryBinding.inflate(layoutInflater)
    }
    private val config: Gallery by lazy {
        intent.getParcelableExtra(GalleryContract.KEY_REQUEST)!!
    }

    private var takePicUri: Uri? = null

    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var albumAdapter: AlbumAdapter

    private val requestMediaPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                loadMedia()
            } else {
                Toast.makeText(this, "请手动授予读取媒体权限", Toast.LENGTH_SHORT).show()
                goToSettings()
            }
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                getTakePictureUri()?.let { takePictureLauncher.launch(it) }
            } else {
                Toast.makeText(this, "请手动授予拍照权限", Toast.LENGTH_SHORT).show()
                goToSettings()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it == true) {
                val mediaEntity = MediaStoreUtil.queryMediaByUri(this, takePicUri)
                mediaEntity?.let {
                    (binding.galleryRv.layoutManager as GridLayoutManager).scrollToPosition(0)
                    galleryAdapter.data.add(0, mediaEntity)
                    galleryAdapter.notifyItemInserted(1)
                    galleryAdapter.notifyItemRangeChanged(1, galleryAdapter.data.size)
                    binding.albumName.text = "全部媒体(${galleryAdapter.data.size})"

                    albumAdapter.data.forEachIndexed { index, albumEntity ->
                        if (albumEntity.albumId == GalleryConstant.ALL_MEDIA_BUCKET_ID || albumEntity.albumId == it.bucketId) {
                            albumAdapter.data.getOrNull(index)?.allMedia?.add(0, mediaEntity)
                            albumAdapter.notifyItemChanged(index)
                        }
                    }

                }
            }
    }

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
        binding.statusView.loading(binding.slidingLayout)
    }

    private fun checkPermission() {
//        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
//        } else {
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (PermissionUtils.checkSelfPermission(context = this, permissions = permissions)) {
            loadMedia()
        } else {
            requestMediaPermissionLauncher.launch(permissions)
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
        galleryAdapter = GalleryAdapter(
            this,
            config,
            onMediaClick = { mediaEntity, selected -> onMediaClick(mediaEntity, selected) },
            onCameraClick = { onCameraClick() }
        )
        binding.galleryRv.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            adapter = galleryAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.csw_layout_animation_from_top)
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

        albumAdapter = AlbumAdapter(this, config, onAlbumClick = { onAlbumClick(it) })
        binding.albumRv.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = albumAdapter
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.albumName -> {
                if (binding.slidingLayout.panelState == PanelState.EXPANDED) {
                    binding.slidingLayout.panelState = PanelState.COLLAPSED
                } else {
                    binding.slidingLayout.panelState = PanelState.EXPANDED
                }
            }
            binding.slidingLayout -> {
                binding.slidingLayout.panelState = PanelState.COLLAPSED
            }
            binding.confirmButton -> {
                val intent = Intent()
                val resources = arrayListOf<Parcelable>().apply {
                    addAll(galleryAdapter.selectedMedia)
                }
                intent.putParcelableArrayListExtra(GalleryContract.KEY_RESULT, resources)

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun onCameraClick() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.CAMERA)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (PermissionUtils.checkSelfPermission(this, permissions = permissions)) {
            getTakePictureUri()?.let { takePictureLauncher.launch(it) }
        } else {
            requestCameraPermissionLauncher.launch(permissions)
        }
    }

    private fun onMediaClick(mediaEntity: MediaEntity, selected: Boolean) {
        if (galleryAdapter.selectedMedia.size == 0) {
            binding.confirmButton.visibility = View.GONE
        } else {
            binding.confirmButton.visibility = View.VISIBLE
            binding.confirmButton.text = "完成(${galleryAdapter.selectedMedia.size})"
        }

        for ((index, value) in albumAdapter.data.withIndex()) {
            if (value.albumId == mediaEntity.bucketId) {
                if (selected) {
                    value.selectedMedia.add(mediaEntity)
                } else {
                    value.selectedMedia.remove(mediaEntity)
                }
                albumAdapter.notifyItemChanged(index)
                break
            }
        }
    }

    private fun onAlbumClick(entity: AlbumEntity) {
        binding.slidingLayout.panelState = PanelState.COLLAPSED
        Handler(Looper.getMainLooper()).postDelayed({
            binding.albumName.text = "${entity.albumRelativePath.dropLast(1)}(${entity.allMedia.size})"
            galleryAdapter.isAllMediaAlbum = entity.albumName == GalleryConstant.ALL_MEDIA_PATH
            galleryAdapter.data = mutableListOf<MediaEntity>().apply { addAll(entity.allMedia) }
            binding.galleryRv.scheduleLayoutAnimation()
        }, 250)
    }

    private fun loadMedia() {
        lifecycleScope.launch(Dispatchers.Main) {
            val galleryEntity = withContext(Dispatchers.IO) {
                MediaStoreUtil.queryMedias(this@GalleryActivity, config)
            }
            galleryAdapter.isAllMediaAlbum = true
            galleryAdapter.data = mutableListOf<MediaEntity>().apply { addAll(galleryEntity.medias) }
            binding.galleryRv.scheduleLayoutAnimation()
            binding.albumName.text = "全部媒体(${galleryEntity.medias.size})"
            albumAdapter.selectedAlbum = GalleryConstant.ALL_MEDIA_PATH
            albumAdapter.data = galleryEntity.albums
            binding.statusView.success()
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

    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun getTakePictureUri(): Uri? {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return if (captureIntent.resolveActivity(packageManager) != null) {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val imageUri = MediaStoreUtil.createImage(applicationContext, fileName)
            if (imageUri != null) {
                takePicUri = imageUri
                imageUri
            } else {
                Toast.makeText(this, "出错了！", Toast.LENGTH_SHORT).show()
                null
            }
        } else {
            Toast.makeText(this, "没有可用于拍照的应用", Toast.LENGTH_SHORT).show()
            null
        }
    }

}