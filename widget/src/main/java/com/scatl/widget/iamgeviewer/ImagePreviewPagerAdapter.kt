package com.scatl.widget.iamgeviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scatl.widget.gallery.MediaEntity

/**
 * Created by sca_tl at 2023/5/8 16:13
 */
class ImagePreviewPagerAdapter(fragmentActivity: FragmentActivity,
                               medias: MutableList<MediaEntity>?) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        medias?.forEachIndexed { index, mediaEntity ->
            fragments.add(ImagePreviewFragment.getInstance(Bundle().apply {
                putSerializable("media", mediaEntity)
                putInt("index", index)
            }))
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}