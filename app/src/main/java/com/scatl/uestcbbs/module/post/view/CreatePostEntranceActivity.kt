package com.scatl.uestcbbs.module.post.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityCreatePostEntranceBinding
import com.scatl.uestcbbs.module.post.presenter.CreatePostEntrancePresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil


/**
 * created by sca_tl at 2023/4/28 18:06
 */
class CreatePostEntranceActivity: BaseVBActivity<CreatePostEntrancePresenter, CreatePostEntranceView, ActivityCreatePostEntranceBinding>(), CreatePostEntranceView {

    private var initX = 0
    private var initY = 0

    override fun getViewBinding() = ActivityCreatePostEntranceBinding .inflate(layoutInflater)

    override fun initPresenter() = CreatePostEntrancePresenter()

    override fun getContext() = this

    override fun getIntent(intent: Intent?) {
        super.getIntent(intent)
        initX = intent?.getIntExtra("x", 0)?: 0
        initY = intent?.getIntExtra("y", 0)?: 0
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        mBinding.rootLayout.visibility = View.INVISIBLE
        mBinding.rootLayout.setBackgroundColor(ColorUtil.getAlphaColor(0.9f, ColorUtil.getAttrColor(this, R.attr.colorOnSurfaceInverse)))

        mBinding.entranceCreateCommonPost.background = GradientDrawable().apply {
            cornerRadius = ScreenUtil.dip2pxF(this@CreatePostEntranceActivity, 15f)
            setColor(Color.parseColor("#25c6da"))
        }

        mBinding.entranceCreateSanshuiPost.background = GradientDrawable().apply {
            cornerRadius = ScreenUtil.dip2pxF(this@CreatePostEntranceActivity, 15f)
            setColor(Color.parseColor("#27bfb2"))
        }

        bindClickEvent(mBinding.close, mBinding.entranceCreateSanshuiPost, mBinding.entranceCreateCommonPost)

        val viewTreeObserver = mBinding.rootLayout.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    revealActivity(initX, initY)
                    mBinding.rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            mBinding.close -> {
                unRevealActivity(mBinding.close)
            }

            mBinding.entranceCreateCommonPost -> {
                unRevealActivity(mBinding.entranceCreateCommonPost)
                val intent = Intent(this, CreatePostActivity::class.java)
                startActivity(intent)
            }

            mBinding.entranceCreateSanshuiPost -> {
                unRevealActivity(mBinding.entranceCreateSanshuiPost)
                val intent = Intent(this, CreatePostActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TYPE, CreatePostActivity.CREATE_TYPE_SANSHUI)
                }
                startActivity(intent)
            }
        }
    }

    private fun revealActivity(x: Int, y: Int) {
        val finalRadius = Math.max(mBinding.rootLayout.width, mBinding.rootLayout.height) * 1.1f

        val circularReveal = ViewAnimationUtils.createCircularReveal(mBinding.rootLayout, x, y, 0f, finalRadius)
        circularReveal.duration = 400
        circularReveal.interpolator = AccelerateInterpolator()

        mBinding.rootLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity(v: View) {
        val loc = intArrayOf(0, 0)
        v.getLocationOnScreen(loc)
        unRevealActivity(loc[0], loc[1])
    }

    private fun unRevealActivity(x: Int, y: Int) {
        val finalRadius = Math.max(mBinding.rootLayout.width, mBinding.rootLayout.height) * 1.1f
        ViewAnimationUtils.createCircularReveal(mBinding.rootLayout, x, y, finalRadius, 0f).apply {
            duration = 400
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    mBinding.rootLayout.visibility = View.INVISIBLE
                    finish()
                }
            })
            start()
        }
    }

    override fun onBackPressed() {
        unRevealActivity(initX, initY)
    }
}