package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.scatl.image.ninelayout.NineGridLayout
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ContentDataType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.custom.ContentImageGetter
import com.scatl.uestcbbs.custom.postview.MyClickableSpan
import com.scatl.uestcbbs.custom.postview.MyImageGetter
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.ContentViewBeanEx
import com.scatl.uestcbbs.module.post.view.VideoPreviewActivity
import com.scatl.uestcbbs.util.*
import com.scatl.uestcbbs.util.DownloadUtil.prepareDownload
import java.util.regex.Pattern


/**
 * Created by sca_tl on 2022/12/6 14:13
 */
@SuppressLint("NotifyDataSetChanged")
class PostContentAdapter(val mContext: Context,
                         val topicId: Int,
                         val onVoteClick: ((ids: MutableList<Int>) -> Unit)?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<ContentViewBean> = mutableListOf()
        set(value) {
            field = value
            mData = convertData(value)
            notifyDataSetChanged()
        }

    var mData: List<ContentViewBeanEx> = mutableListOf()
        private set

    private fun convertData(origin: List<ContentViewBean>): List<ContentViewBeanEx> {
        val result = mutableListOf<ContentViewBeanEx>()
        origin.forEach {
            when(it.type) {
                ContentDataType.TYPE_ATTACHMENT -> {
                    if (FileUtil.isPicture(it.originalInfo)) {
                        if (result.isNotEmpty() && result.last().type == ContentDataType.TYPE_IMAGE) {
                            if (result.last().images == null) {
                                result.last().images = mutableListOf()
                            }
                            if (!result.last().images.contains(it.originalInfo)) {
                                result.last().images.add(it.originalInfo)
                            }
                        } else {
                            val tmp = ContentViewBeanEx().apply {
                                type = ContentDataType.TYPE_IMAGE
                                infor = it.infor
                                url = it.url
                                desc = it.desc
                                originalInfo = it.originalInfo
                                aid = it.aid
                            }
                            if (tmp.images == null) {
                                tmp.images = mutableListOf()
                            }
                            tmp.images.add(it.originalInfo)
                            result.add(tmp)
                        }
                    } else {
                        result.add(ContentViewBeanEx().apply {
                            type = ContentDataType.TYPE_ATTACHMENT
                            infor = it.infor
                            url = it.url
                            desc = it.desc
                            originalInfo = it.originalInfo
                            aid = it.aid
                        })
                    }
                }
                ContentDataType.TYPE_IMAGE -> {
                    if (result.isNotEmpty() && result.last().type == ContentDataType.TYPE_IMAGE) {
                        if (result.last().images == null) {
                            result.last().images = mutableListOf()
                        }
                        if (!result.last().images.contains(it.originalInfo)) {
                            result.last().images.add(it.originalInfo)
                        }
                    } else {
                        val tmp = ContentViewBeanEx().apply {
                            type = ContentDataType.TYPE_IMAGE
                            infor = it.infor
                            url = it.url
                            desc = it.desc
                            originalInfo = it.originalInfo
                            aid = it.aid
                        }
                        if (tmp.images == null) {
                            tmp.images = mutableListOf()
                        }
                        tmp.images.add(it.originalInfo)
                        result.add(tmp)
                    }
                }
                else -> {
                    result.add(ContentViewBeanEx().apply {
                        type = it.type
                        infor = it.infor
                        url = it.url
                        desc = it.desc
                        originalInfo = it.originalInfo
                        aid = it.aid
                        mPollInfoBean = it.mPollInfoBean
                    })
                }
            }
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            ContentDataType.TYPE_TEXT -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_text, parent, false)
                return TextViewHolder(view)
            }
            ContentDataType.TYPE_IMAGE -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_image, parent, false)
                return ImageViewHolder(view)
            }
            ContentDataType.TYPE_ATTACHMENT -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_attachment, parent, false)
                return AttachmentViewHolder(view)
            }
            ContentDataType.TYPE_URL -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_link, parent, false)
                return LinkViewHolder(view)
            }
            ContentDataType.TYPE_VOTE -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_vote, parent, false)
                return VoteViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_attachment, parent, false)
                return AttachmentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            ContentDataType.TYPE_TEXT -> {
                setText(holder as TextViewHolder, position)
            }
            ContentDataType.TYPE_IMAGE -> {
                setImage(holder as ImageViewHolder, position)
            }
            ContentDataType.TYPE_ATTACHMENT -> {
                setAttachment(holder as AttachmentViewHolder, position)
            }
            ContentDataType.TYPE_URL -> {
                setLink(holder as LinkViewHolder, position)
            }
            ContentDataType.TYPE_VOTE -> {
                setVote(holder as VoteViewHolder, position)
            }
        }
    }

    private fun setImage(holder: ImageViewHolder, position: Int) {
        holder.nineImageLayout.setNineGridAdapter(NineImageAdapter(mData[position].images))
    }

    private fun setText(holder: TextViewHolder, position: Int) {
        var textData = mData[position].infor

        val modifyMatcher = Pattern.compile("本帖最后由(.*?)于(.*?)编辑").matcher(textData)
        if (modifyMatcher.find()) {
            textData = textData.replace(modifyMatcher.group(), "")
            holder.modifyCard.visibility = View.VISIBLE
            holder.modifyText.text = modifyMatcher.group()
        } else {
            holder.modifyCard.visibility = View.GONE
        }

        val emotionMatcher = Pattern.compile("(\\[mobcent_phiz=(.*?)])").matcher(textData)
        if (emotionMatcher.find()) {
            do {
//                val a = EmotionManager.INSTANCE.getLocalPath(emotionMatcher.group(2))
                textData = textData.replaceFirst(emotionMatcher.group(0), "<img src = " + emotionMatcher.group(2) + ">")
            } while (emotionMatcher.find())
        }

        if (textData.startsWith(" ")) {
            do {
                textData = textData.replaceFirst(" ", "")
            } while (textData.startsWith(" "))
        }

        if (textData.startsWith("\r\n")) {
            do {
                textData = textData.replaceFirst("\r\n", "")
            } while (textData.startsWith("\r\n"))
        }

        textData = textData.replace("\r\n", "<br>")
        holder.text.text = Html.fromHtml(textData, MyImageGetter(mContext, holder.text), null)
    }

    private fun setAttachment(holder: AttachmentViewHolder, position: Int) {
        holder.name.text = mData[position].infor
        holder.desc.text = mData[position].desc
        holder.itemView.setOnClickListener {
            if (FileUtil.isVideo(mData[position].infor)) {
                val intent = Intent(mContext, VideoPreviewActivity::class.java).apply {
                    putExtra(Constant.IntentKey.FILE_NAME, mData[position].infor)
                    putExtra(Constant.IntentKey.URL, mData[position].url)
                }
                mContext.startActivity(intent)
            } else {
                prepareDownload(mContext, mData[position].infor, mData[position].url)
            }
        }
    }

    private fun setLink(holder: LinkViewHolder, position: Int) {
        val spannableString = SpannableString(mData[position].infor)
        val clickableSpan = MyClickableSpan(mContext, mData[position].url)
        spannableString.setSpan(clickableSpan, 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        holder.link.movementMethod = LinkMovementMethod.getInstance()
        holder.link.text = spannableString
    }

    private fun setVote(holder: VoteViewHolder, position: Int) {
        val voteData = mData[position].mPollInfoBean
        val adapter = ContentViewPollAdapter(R.layout.item_content_view_poll)
        holder.recyclerView.adapter = adapter
        adapter.addPollData(voteData.poll_item_list, voteData.voters, voteData.poll_status)

        when(voteData.poll_status) {
            1 -> {
                holder.dsp.text = mContext.resources.getString(R.string.is_voted)
                holder.submit.visibility = View.GONE
            }
            2 -> {
                holder.dsp.text = mContext.resources.getString(R.string.can_vote, voteData.type)
                holder.submit.visibility = View.VISIBLE
                holder.submit.setOnClickListener {
                    when(adapter.pollItemIds.size) {
                        in Int.MIN_VALUE..0 -> {
                            ToastUtil.showToast(mContext, "至少选择1项", ToastType.TYPE_ERROR)
                        }
                        in (voteData.type + 1)..Int.MAX_VALUE -> {
                            ToastUtil.showToast(mContext, "至多选择${voteData.type}项", ToastType.TYPE_ERROR)
                        }
                        else -> {
                            onVoteClick?.let { it1 -> it1(adapter.pollItemIds) }
                        }
                    }
                }
            }
            3 -> {
                holder.dsp.text = mContext.resources.getString(R.string.no_vote_permission)
                holder.submit.visibility = View.GONE
            }
            4 -> {
                holder.dsp.text = mContext.resources.getString(R.string.vote_closed)
                holder.submit.visibility = View.GONE
            }
        }
        val spannableString = SpannableString(mContext.resources.getString(R.string.total_voters, voteData.voters))
        spannableString.setSpan(MyClickableSpan(mContext, Constant.VIEW_VOTER_LINK), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        holder.dsp.apply {
            movementMethod = LinkMovementMethod.getInstance()
            append(", ")
            append(spannableString)
            tag = Bundle().also {
                it.putInt(Constant.IntentKey.TOPIC_ID, topicId)
            }
        }
    }

    override fun getItemViewType(position: Int) = mData[position].type

    override fun getItemCount() = mData.size

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val modifyCard: MaterialCardView = itemView.findViewById(R.id.modify_card)
        val modifyText: TextView = itemView.findViewById(R.id.modify_text)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nineImageLayout: NineGridLayout = itemView.findViewById(R.id.nine_image_layout)
    }

    class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val desc: TextView = itemView.findViewById(R.id.desc)
    }

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val link: TextView = itemView.findViewById(R.id.link)
    }

    class VoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        val dsp: TextView = itemView.findViewById(R.id.dsp)
        val submit: MaterialButton = itemView.findViewById(R.id.submit)
    }
}