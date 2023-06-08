package com.scatl.uestcbbs.module.post

import android.content.Context
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.PostDetailBean.TopicBean.ContentBean
import com.scatl.uestcbbs.entity.PostDetailBean.TopicBean.PollInfoBean

/**
 * Created by sca_tl at 2023/6/7 19:46
 */
class PostDetailAdapter(val context: Context,
                        data: MutableList<MultiItemEntity>)
    : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    var postDetailBean: PostDetailBean? = null

    companion object {
        const val TYPE_POST_INFO = 7

        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
        const val TYPE_VIDEO = 2
        const val TYPE_AUDIO = 3
        const val TYPE_URL = 4
        const val TYPE_ATTACHMENT = 5
        const val TYPE_VOTE = 6

        const val TYPE_EXTRA_INFO = 8
        const val TYPE_VIEWPAGER = 9
    }

    init {
        addItemType(TYPE_POST_INFO, 0)
        addItemType(TYPE_TEXT, 0)
        addItemType(TYPE_IMAGE, 0)
        addItemType(TYPE_VIDEO, 0)
        addItemType(TYPE_AUDIO, 0)
        addItemType(TYPE_URL, 0)
        addItemType(TYPE_ATTACHMENT, 0)
        addItemType(TYPE_VOTE, 0)
        addItemType(TYPE_EXTRA_INFO, 0)
        addItemType(TYPE_VIEWPAGER, 0)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when(helper.itemViewType) {
            TYPE_POST_INFO -> { convertPostInfo(helper, item as PostDetailBean) }
            TYPE_TEXT -> { convertText(helper, item as ContentBean) }
            TYPE_IMAGE -> { convertImage(helper, item as ContentBean) }
            TYPE_VIDEO -> { convertVideo(helper, item as ContentBean) }
            TYPE_AUDIO -> { convertAudio(helper, item as ContentBean) }
            TYPE_URL -> { convertUrl(helper, item as ContentBean) }
            TYPE_ATTACHMENT -> { convertAttachment(helper, item as ContentBean) }
            TYPE_VOTE -> { convertVote(helper, item as PollInfoBean) }
            TYPE_EXTRA_INFO -> { convertExtraInfo(helper, item as ExtraEntity) }
            TYPE_VIEWPAGER -> { convertViewPager(helper, item as ViewPagerEntity) }
        }
    }

    private fun convertPostInfo(helper: BaseViewHolder, postDetailBean: PostDetailBean) {

    }

    private fun convertText(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertImage(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertVideo(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertAudio(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertUrl(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertAttachment(helper: BaseViewHolder, contentBean: ContentBean) {

    }

    private fun convertVote(helper: BaseViewHolder, pollInfoBean: PollInfoBean) {

    }

    private fun convertExtraInfo(helper: BaseViewHolder, extraEntity: ExtraEntity) {

    }

    private fun convertViewPager(helper: BaseViewHolder, viewPagerEntity: ViewPagerEntity) {

    }

    class ViewPagerEntity : MultiItemEntity {
        override fun getItemType() = TYPE_VIEWPAGER
    }

    class ExtraEntity : MultiItemEntity {
        override fun getItemType() = TYPE_EXTRA_INFO
    }
}