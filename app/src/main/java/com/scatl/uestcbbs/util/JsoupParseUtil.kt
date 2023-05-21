package com.scatl.uestcbbs.util

import com.scatl.uestcbbs.entity.CollectionListBean
import org.jsoup.Jsoup
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/5/19 10:39
 */
object JsoupParseUtil {

    @JvmStatic
    fun parseCollectionList(html: String?): List<CollectionListBean> {
        val collectionBeans: MutableList<CollectionListBean> = ArrayList()

        if (html.isNullOrEmpty()) {
            return collectionBeans
        }

        try {

            val document = Jsoup.parse(html)
            val elements = document.select("div[class=clct_list cl]").select("div[class=xld xlda cl]")

            for (i in elements.indices) {
                val collectionBean = CollectionListBean().apply {
                    collectionTags = ArrayList()
                }

                val dd1Data = elements[i].select("dd[class=m hm]")
                collectionBean.collectionLink = dd1Data.select("a").attr("href")
                collectionBean.collectionId = BBSLinkUtil.getLinkInfo(collectionBean.collectionLink).id

                val dtData = elements[i].select("dt[class=xw1]")
                collectionBean.collectionTags = dtData.select("span[class=ctag_keyword]").select("a").eachText()
                collectionBean.createByMe = dtData.text().contains("我创建的")
                collectionBean.subscribeByMe = dtData.text().contains("我订阅的")
                if (dtData.select("a").size > 0) {
                    collectionBean.collectionTitle = dtData.select("a")[0].text()
                    collectionBean.hasUnreadPost = dtData.select("a")[0].attr("style").contains("red") && collectionBean.subscribeByMe
                }

                if (elements[i].select("dd").size > 1) {
                    val dd2Data = elements[i].select("dd")[1]

                    collectionBean.authorLink = dd2Data.select("p[class=xg1]").select("a").attr("href")
                    collectionBean.authorId = BBSLinkUtil.getLinkInfo(collectionBean.authorLink).id
                    collectionBean.authorName = dd2Data.select("p[class=xg1]").select("a").text()
                    collectionBean.authorAvatar = Constant.USER_AVATAR_URL.plus(collectionBean.authorId)
                    collectionBean.latestUpdateDate = dd2Data.select("p[class=xg1]")[0].ownText().replace("创建, 最后更新 ", "")

                    if (dd2Data.select("p").size > 0) {
                        collectionBean.collectionDsp = dd2Data.select("p")[0].text()
                    }

                    when(dd1Data.select("span").text()) {
                        "主题" -> {
                            collectionBean.postCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("订阅 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.subscribeCount = matcher.group(1)
                                collectionBean.commentCount = matcher.group(2)
                            }
                        }
                        "评论" -> {
                            collectionBean.commentCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("订阅 (\\d+), 主题 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.subscribeCount = matcher.group(1)
                                collectionBean.postCount = matcher.group(2)
                            }
                        }
                        "订阅" -> {
                            collectionBean.subscribeCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("主题 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.postCount = matcher.group(1)
                                collectionBean.commentCount = matcher.group(2)
                            }
                        }
                    }
                    if (dd2Data.select("p").size > 3) {
                        collectionBean.latestPostTitle = dd2Data.select("p")[3].select("a").text()
                        collectionBean.latestPostLink = dd2Data.select("p")[3].select("a").attr("href")
                        collectionBean.latestPostId = BBSLinkUtil.getLinkInfo(collectionBean.latestPostLink).id
                    }
                }

                collectionBeans.add(collectionBean)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return collectionBeans
    }

}