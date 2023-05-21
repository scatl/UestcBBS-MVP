package com.scatl.uestcbbs.widget.emotion

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.databinding.LayoutEmotionPanelBinding
import com.scatl.uestcbbs.util.DebugUtil
import com.scatl.uestcbbs.util.desensitize
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl on 2023/1/6 10:10
 */
class EmotionPanelLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    private var mBinding = LayoutEmotionPanelBinding.inflate(LayoutInflater.from(getContext()), this, true)

    init {
        initEmoticonPanel()
    }

    private fun initEmoticonPanel() {
        val tabImages: MutableList<String> = ArrayList()
        val emotions: ArrayList<ArrayList<String>> = ArrayList()

        for (i in 0..8) {
            val imgPath = ArrayList<String>()

            val s = context.assets.list("emotion/" + (i + 1))
            for (j in s!!.indices) {
                imgPath.add("file:///android_asset/emotion/" + (i + 1) + "/" + s[j])
            }

            emotions.add(imgPath)
            tabImages.add(imgPath[1])
        }

        val columns = ScreenUtil.getScreenWidth(context, true) / 60

        mBinding.viewPager2.apply {
            desensitize()
            offscreenPageLimit = 2
            adapter = EmotionPanelAdapter(context,columns, emotions)
            currentItem = 0
        }

        tabImages.forEach {
            val tab = mBinding.tabLayout.newTab()
            mBinding.tabLayout.addTab(tab)
        }

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2) { tab: TabLayout.Tab, position: Int ->
            try {
                val view = LayoutInflater.from(context).inflate(R.layout.item_emotion_tab_view, null)
                val imageView = view.findViewById<ImageView>(R.id.image)
                Glide.with(context).load(tabImages[position]).into(imageView)
                tab.customView = view
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }.attach()
    }

}