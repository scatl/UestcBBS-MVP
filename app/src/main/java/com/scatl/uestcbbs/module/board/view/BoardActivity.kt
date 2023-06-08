package com.scatl.uestcbbs.module.board.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.MenuItem
import android.view.View
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayoutMediator
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityNewBoardBinding
import com.scatl.uestcbbs.entity.ForumDetailBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.manager.ForumListManager
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector
import com.scatl.uestcbbs.module.board.adapter.BoardPostViewPagerAdapter
import com.scatl.uestcbbs.module.board.presenter.BoardPresenter
import com.scatl.uestcbbs.module.board.view.behavior.CoverBehavior
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ColorUtil
import com.scatl.util.ImageUtil
import com.scatl.util.desensitize
import java.io.File
import java.io.IOException

/**
 * Created by sca_tl at 2023/4/27 10:35
 */
class BoardActivity: BaseVBActivity<BoardPresenter, BoardView, ActivityNewBoardBinding>(),
    BoardView, CoverBehavior.OnCoverViewChanged {

    private var boardId = 0
    private var locateBoardId = 0
    private lateinit var boardName: String
    private var deprecatedBoard = false

    override fun getViewBinding() = ActivityNewBoardBinding.inflate(layoutInflater)

    override fun initPresenter() = BoardPresenter()

    override fun getIntent(intent: Intent?) {
        boardId = intent?.getIntExtra(Constant.IntentKey.BOARD_ID, Int.MAX_VALUE)?:Int.MAX_VALUE
        boardName = intent?.getStringExtra(Constant.IntentKey.BOARD_NAME) ?: ""
        locateBoardId = intent?.getIntExtra(Constant.IntentKey.LOCATE_BOARD_ID, Int.MAX_VALUE)?:Int.MAX_VALUE

        val tmpName = ForumListManager.INSTANCE.getForumInfo(boardId).name ?: ""
        if (tmpName.isNotEmpty()) {
            boardName = tmpName
        }
        if (boardId == 0) {
            deprecatedBoard = true
            boardId = locateBoardId
        }
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)

        if (boardId == 0 || boardId == Int.MAX_VALUE) {
            showToast("板块不存在！", ToastType.TYPE_ERROR)
            finish()
        }

        mBinding.toolbar.title = boardName
        mBinding.boardName.text = boardName
        mBinding.viewpager.desensitize()

        bindClickEvent(mBinding.coverImg)

        loadBoardImg()
        mPresenter?.getForumDetail(boardId)
        mPresenter?.getSubBoardList(boardId)

        mBinding.statusView.loading(mBinding.coverLayout, mBinding.appBar, mBinding.contentLayout)
    }

    override fun onClick(v: View) {
        if (v == mBinding.coverImg) {
            PictureSelector
                .create(this)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(false)
                .isGif(false)
                .showCropFrame(true)
                .hideBottomControls(false)
                .theme(R.style.picture_WeChat_style)
                .maxSelectNum(1)
                .isEnableCrop(true)
                .withAspectRatio(3, 2)
                .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                .forResult(PictureConfig.CHOOSE_REQUEST)
        }
    }

    override fun onOptionsSelected(item: MenuItem?) {
        super.onOptionsSelected(item)
        if (item?.itemId == R.id.menu_board_set_background_default) {
            SharePrefUtil.setBoardImg(this, boardId, "file:///android_asset/board_img/$boardId.jpg")
            loadBoardImg()
        }
    }

    override fun onGetSubBoardListSuccess(subForumListBean: SubForumListBean) {
        val ids = mutableListOf(boardId)
        val titles = mutableListOf(boardName)

        if (!subForumListBean.list.isNullOrEmpty()) {
            for (item in subForumListBean.list[0].board_list) {
                titles.add(item.board_name)
                ids.add(item.board_id)
            }
        }

        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = BoardPostViewPagerAdapter(this@BoardActivity, ids)
            currentItem = 0
        }

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        if (locateBoardId != Int.MAX_VALUE) {
            val locateIndex = ids.indexOf(locateBoardId)
            if (locateIndex in 0 until ids.size) {
                mBinding.viewpager.setCurrentItem(locateIndex, false)
            }
            locateBoardId = Int.MAX_VALUE
        }

        mBinding.statusView.success()
    }

    override fun onGetSubBoardListError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    override fun onGetForumDetailSuccess(forumDetailBean: ForumDetailBean?) {
    }

    private fun getBoardImgPath() = SharePrefUtil.getBoardImg(this, if (deprecatedBoard) 0 else boardId)

    private fun loadBoardImg() {
        mBinding.boardIcon.load(getBoardImgPath())
        setBlurBg()
        try {
            Glide
                .with(this)
                .load(getBoardImgPath())
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                        val bitmap = if (resource is GifDrawable) resource.firstFrame else ImageUtil.drawable2Bitmap(resource)
                        bitmap?.let {
                            mBinding.coverImg.setImageBitmap(ImageUtil.blur(this@BoardActivity, it, 15f))
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBlurBg() {
        val colors = intArrayOf(
            Color.parseColor("#4D000000"),
            ColorUtil.getAlphaColor(0.6f, Color.parseColor("#4D000000"))
        )

        try {
            if (SharePrefUtil.getBoardImg(getContext(), boardId).contains("android_asset")) {
                getContext()
                    .resources
                    .assets
                    .open(SharePrefUtil.getBoardImg(getContext(), boardId).replace("file:///android_asset/", ""))
            } else {
                File(SharePrefUtil.getBoardImg(getContext(), boardId)).inputStream()
            }.use {
                val bitmap = BitmapFactory.decodeStream(it)
                val palette = Palette.from(bitmap).generate()
                val vibrantSwatch = palette.vibrantSwatch
                val mutedSwatch = palette.mutedSwatch

                if (mutedSwatch != null) {
                    colors[0] = mutedSwatch.rgb
                    colors[1] = ColorUtil.getAlphaColor(0.6f, mutedSwatch.rgb)
                } else if (vibrantSwatch != null) {
                    colors[0] = vibrantSwatch.rgb
                    colors[1] = ColorUtil.getAlphaColor(0.6f, vibrantSwatch.rgb)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mBinding.blurImg.background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
    }

    override fun getContext() = this

    override fun setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null)
    }

    override fun onCoverChanged(upPercent: Float, downPercent: Float) {
        mBinding.boardIcon.alpha = 1 - upPercent
        mBinding.boardName.alpha = 1 - upPercent
        mBinding.coverImg.alpha = 1 - upPercent
        mBinding.blurImg.alpha = upPercent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList.size != 0 && selectList[0].isCut) {
                SharePrefUtil.setBoardImg(this, boardId, selectList[0].cutPath)
                loadBoardImg()
            }
        }
    }
}