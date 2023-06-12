package com.scatl.uestcbbs.module.home.adapter.latestpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.databinding.HomeItemBannerViewBinding
import com.scatl.uestcbbs.entity.BingPicBean
import com.scatl.uestcbbs.helper.BaseOneItemAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Banner
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.widget.download.DownloadManager
import com.youth.banner.BannerConfig

/**
 * Created by sca_tl at 2023/6/12 15:56
 */
class BannerAdapter: BaseOneItemAdapter<BingPicBean, HomeItemBannerViewBinding>() {

    override fun getViewBinding(parent: ViewGroup): HomeItemBannerViewBinding {
        return HomeItemBannerViewBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<HomeItemBannerViewBinding>, item: BingPicBean?) {
        super.onBindViewHolder(holder, item)
        if (item == null) {
            return
        }

        val imgUrls: MutableList<String> = ArrayList()
        val imgTitles: MutableList<String> = ArrayList()

        for (i in item.images.indices) {
            imgUrls.add(ApiConstant.BING_BASE_URL + item.images[i].url)
            imgTitles.add(item.images[i].copyright)
        }

        if (SharePrefUtil.isShowHomeBanner(context)) {
            holder.binding.banner.visibility = View.VISIBLE
            holder.binding.banner
                .setImages(imgUrls)
                .setBannerTitles(imgTitles)
                .setImageLoader(GlideLoader4Banner())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setDelayTime(3000)
                .setOnBannerListener { position: Int ->
                    val imgUrl = imgUrls[position]
                    val imgCopyRight = imgTitles[position]
                    DownloadManager
                        .Companion
                        .with(context)
                        .setTitle("必应每日一图高清图片下载")
                        .setUrl(imgUrl.replaceFirst("1920x1080".toRegex(), "UHD"))
                        .setName(imgCopyRight.replace("/", "_") + ".jpg")
                        .start()
                }
                .start()
        } else {
            holder.binding.banner.visibility = View.GONE
        }
    }
}