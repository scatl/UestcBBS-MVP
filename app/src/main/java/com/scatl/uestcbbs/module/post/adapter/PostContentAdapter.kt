package com.scatl.uestcbbs.module.post.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.scatl.widget.ninelayout.NineGridLayout
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ContentDataType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.ContentViewBeanEx
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.module.post.view.CopyContentFragment
import com.scatl.uestcbbs.module.post.view.ViewOriginCommentFragment
import com.scatl.uestcbbs.util.*
import com.scatl.uestcbbs.widget.span.CustomClickableSpan
import com.scatl.uestcbbs.widget.textview.EmojiTextView
import com.scatl.util.ColorUtil
import com.scatl.widget.audioplay.AudioPlayService
import com.scatl.widget.audioplay.AudioPlayer
import com.scatl.widget.download.DownloadManager
import com.scatl.widget.video.VideoPreViewManager
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

    var type = TYPE.TOPIC

    var comments: List<PostDetailBean.ListBean>? = null

    var mData: List<ContentViewBeanEx> = mutableListOf()
        private set

    val mImages = mutableListOf<String>()

    val mWholeText = StringBuilder()

    private fun convertData(origin: List<ContentViewBean>): List<ContentViewBeanEx> {
        val result = mutableListOf<ContentViewBeanEx>()
        origin.forEach {
            when(it.type) {
                ContentDataType.TYPE_ATTACHMENT -> {
                    if (!FileUtil.isPicture(it.infor) && !mImages.contains(it.originalInfo) &&
                        !it.infor.isNullOrEmpty() && !it.url.isNullOrEmpty()) {
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
                            mImages.add(it.originalInfo)
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
                        mImages.add(it.originalInfo)
                        result.add(tmp)
                    }
                }
                else -> {
                    if (it.type == ContentDataType.TYPE_TEXT) {
                        mWholeText.append(it.infor)
                    }
                    if (it.type == ContentDataType.TYPE_URL) {
                        mWholeText.append(it.url)
                    }
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
            ContentDataType.TYPE_AUDIO -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_content_audio, parent, false)
                return AudioViewHolder(view)
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
            ContentDataType.TYPE_AUDIO -> {
                setAudio(holder as AudioViewHolder, position)
            }
        }
    }

    private fun setAudio(holder: AudioViewHolder, position: Int) {
        val url = mData[position].infor
        val playing = AudioPlayer.INSTANCE.isPlaying(url)
        holder.itemView.setOnClickListener {
            if (playing) {
                mContext.showToast("正在播放该音频", ToastType.TYPE_ERROR)
            } else {
                val intent = Intent(mContext, AudioPlayService::class.java).apply {
                    putExtra("url", url)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(intent)
                } else {
                    mContext.startService(intent)
                }
                mContext.showToast("后台播放中", ToastType.TYPE_NORMAL)
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

        holder.text.setText(textData)
        holder.text.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu.add("复制全文").setOnMenuItemClickListener {
                ClipBoardUtil.copyToClipBoard(mContext, mWholeText.toString())
                true
            }
            menu.add("自由复制").setOnMenuItemClickListener {
                CopyContentFragment
                    .getInstance(Bundle().apply {
                        putString(Constant.IntentKey.CONTENT, mWholeText.toString())
                    })
                    .show((mContext as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                true
            }
        }
    }

    private fun setAttachment(holder: AttachmentViewHolder, position: Int) {
        holder.name.text = mData[position].infor
        holder.desc.text = mData[position].desc
        holder.itemView.setOnClickListener {
            if (FileUtil.isVideo(mData[position].infor)) {
                VideoPreViewManager
                    .INSTANCE
                    .with(mContext)
                    .setUrl(mData[position].url)
                    .setName(mData[position].infor)
                    .setCookies(RetrofitCookieUtil.getCookies())
                    .start()
            } else {
                DownloadManager
                    .with(mContext)
                    .setName(mData[position].infor)
                    .setUrl(mData[position].url)
                    .setCookies(RetrofitCookieUtil.getCookies())
                    .start()
            }
        }

        if (FileUtil.isVideo(mData[position].infor)) {
            holder.icon.setImageResource(R.drawable.ic_video)
            holder.desc.text = "点击观看[${mData[position].desc}]"
        }
        if (FileUtil.isAudio(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_music)
        if (FileUtil.isCompressed(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_compressed)
        if (FileUtil.isApplication(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_app)
        if (FileUtil.isPlugIn(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_plugin)
        if (FileUtil.isPdf(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_pdf)
        if (FileUtil.isDocument(mData[position].infor)) holder.icon.setImageResource(R.drawable.ic_document)
    }

    private fun setLink(holder: LinkViewHolder, position: Int) {
        val spannableString = SpannableStringBuilder(mData[position].infor)
        val pid = BBSLinkUtil.getLinkInfo(mData[position].url).pid
        val originCommentData = if (comments != null) CommentUtil.findCommentByPid(comments, pid.toString()) else null

        if (originCommentData != null && mContext is FragmentActivity) {
            spannableString.apply {
                setSpan(ForegroundColorSpan(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary)),
                    0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                setSpan(UnderlineSpan(), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            holder.link.setOnClickListener {
                ViewOriginCommentFragment
                    .getInstance(Bundle().apply {
                        putInt(Constant.IntentKey.TOPIC_ID, topicId)
                        putSerializable(Constant.IntentKey.DATA_1, originCommentData)
                    })
                    .show(mContext.supportFragmentManager, TimeUtil.getStringMs())
            }
        } else {
            spannableString.setSpan(
                CustomClickableSpan(mContext, mData[position].url),
                0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            holder.link.movementMethod = LinkMovementMethod.getInstance()
        }

        holder.link.text = spannableString

        holder.link.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu.add("复制链接").setOnMenuItemClickListener {
                ClipBoardUtil.copyToClipBoard(mContext, mData[position].url)
                true
            }
        }
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
        spannableString.setSpan(
            CustomClickableSpan(mContext, Constant.VIEW_VOTER_LINK.plus(topicId)),
            0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        holder.dsp.apply {
            movementMethod = LinkMovementMethod.getInstance()
            append("\n")
            append(spannableString)
        }
    }

    override fun getItemViewType(position: Int) = mData[position].type

    override fun getItemCount() = mData.size

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: EmojiTextView = itemView.findViewById(R.id.text)
        val modifyCard: MaterialCardView = itemView.findViewById(R.id.modify_card)
        val modifyText: TextView = itemView.findViewById(R.id.modify_text)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nineImageLayout: NineGridLayout = itemView.findViewById(R.id.nine_image_layout)
    }

    class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val desc: TextView = itemView.findViewById(R.id.desc)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val link: TextView = itemView.findViewById(R.id.link)
    }

    class VoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        val dsp: TextView = itemView.findViewById(R.id.dsp)
        val submit: MaterialButton = itemView.findViewById(R.id.submit)
    }

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dsp: TextView = itemView.findViewById(R.id.dsp)
    }

    enum class TYPE {
        TOPIC, REPLY, QUOTE, VIEW_ORIGIN
    }
}