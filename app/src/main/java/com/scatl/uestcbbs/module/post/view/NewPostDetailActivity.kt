package com.scatl.uestcbbs.module.post.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ContentDataType
import com.scatl.uestcbbs.annotation.PostAppendType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.base.BaseVBFragmentForBottom
import com.scatl.uestcbbs.databinding.ActivityNewPostDetailBinding
import com.scatl.uestcbbs.entity.*
import com.scatl.uestcbbs.module.collection.view.AddToCollectionFragment
import com.scatl.uestcbbs.module.collection.view.CollectionDetailActivity
import com.scatl.uestcbbs.module.credit.view.CreditHistoryActivity
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment
import com.scatl.uestcbbs.module.post.adapter.DianPingAdapter
import com.scatl.uestcbbs.module.post.adapter.NewPostDetailPagerAdapter
import com.scatl.uestcbbs.module.post.adapter.PostCollectionAdapter
import com.scatl.uestcbbs.module.post.adapter.PostContentAdapter
import com.scatl.uestcbbs.module.post.presenter.NewPostDetailPresenter
import com.scatl.uestcbbs.module.report.ReportFragment
import com.scatl.uestcbbs.module.search.view.SearchActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.module.webview.view.WebViewActivity
import com.scatl.uestcbbs.util.*
import com.scatl.util.ImageUtil
import com.scatl.util.ColorUtil
import com.scatl.util.NumberUtil
import com.scatl.util.ScreenUtil
import com.scatl.util.desensitize
import com.scatl.widget.SmoothNestedScrollLayout
import com.scatl.widget.dialog.InputAlertDialogBuilder
import org.greenrobot.eventbus.EventBus
import java.util.regex.Pattern
import kotlin.concurrent.thread

const val TAG = "NewPostDetailActivity"

@SuppressLint("SetTextI18n")
class NewPostDetailActivity : BaseVBActivity<NewPostDetailPresenter, NewPostDetailView, ActivityNewPostDetailBinding>(), NewPostDetailView, SmoothNestedScrollLayout.onScrollListener {
    private var topicId: Int = Int.MAX_VALUE
    private var postId: Int = Int.MAX_VALUE
    private var boardId: Int = Int.MAX_VALUE
    private var userId: Int = Int.MAX_VALUE
    private var locateComment: Bundle? = null
    private var pingjiaCount: Int = 0
    private var currentSort = CommentFragment.SORT.DEFAULT
    private var postDetailBean: PostDetailBean? = null
    private lateinit var postContentAdapter: PostContentAdapter
    private lateinit var postCollectionAdapter: PostCollectionAdapter
    private lateinit var postDianPingAdapter: DianPingAdapter

    override fun getIntent(intent: Intent?) {
        intent?.let {
            topicId = it.getIntExtra(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
            locateComment = it.getBundleExtra(Constant.IntentKey.LOCATE_COMMENT)
        }
    }

    override fun getViewBinding() = ActivityNewPostDetailBinding.inflate(layoutInflater)

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        mBinding.scrollLayout.apply {
            setTopView(mBinding.headView)
            setContentView(mBinding.viewpager)
            setInnerOffsetView(mBinding.tabLayout)
            setScrollListener(this@NewPostDetailActivity)
        }
        mBinding.viewpager.registerOnPageChangeCallback(mPageChangeCallback)
        postCollectionAdapter = PostCollectionAdapter(R.layout.item_post_detail_collection)
        postDianPingAdapter = DianPingAdapter(R.layout.item_post_detail_dianping)
        mBinding.dianpingRv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = ScreenUtil.dip2px(getContext(), 10f)
            }
        })
        bindClickEvent(
            mBinding.collectBtn, mBinding.supportBtn, mBinding.againstBtn, mBinding.commentBtn, mBinding.dianpingBtn,
            mBinding.pingfenBtn, mBinding.buchongBtn, mBinding.avatar, mBinding.rewardInfoBtn, mBinding.warningLayout
        )

        mBinding.statusView.loading(mBinding.scrollLayout, mBinding.bottomLayout)
        mPresenter?.getDetail(1, 0, 0, topicId, 0)
    }

    override fun initPresenter() = NewPostDetailPresenter()

    override fun getContext() = this

    override fun onClick(v: View) {
        when(v) {
            mBinding.collectBtn -> {
                mPresenter?.favorite("tid", if (postDetailBean?.topic?.is_favor == 1) "delfavorite" else "favorite", topicId)
            }
            mBinding.supportBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mPresenter?.support(topicId, postId, "thread", "support")
            }
            mBinding.againstBtn -> {
                mPresenter?.support(topicId, postId, "thread", "against")
            }
            mBinding.commentBtn -> {
                val intent = Intent(this, CreateCommentActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, postDetailBean?.boardId)
                    putExtra(Constant.IntentKey.TOPIC_ID, topicId)
                    putExtra(Constant.IntentKey.QUOTE_ID, 0)
                    putExtra(Constant.IntentKey.IS_QUOTE, false)
                    putExtra(Constant.IntentKey.USER_NAME, postDetailBean?.topic?.user_nick_name)
                    putExtra(Constant.IntentKey.POSITION, -1)
                }
                startActivity(intent)
                mBinding.viewpager.setCurrentItem(1, true)
            }
            mBinding.dianpingBtn -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.POST_ID, postId)
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                    putString(Constant.IntentKey.TYPE, PostAppendType.DIANPING)
                }
                PostAppendFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
                mBinding.viewpager.setCurrentItem(2, true)
            }
            mBinding.pingfenBtn -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                    putInt(Constant.IntentKey.POST_ID, postId)
                }
                PostRateFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
                mBinding.viewpager.setCurrentItem(3, true)
            }
            mBinding.buchongBtn -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.POST_ID, postId)
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                    putString(Constant.IntentKey.TYPE, PostAppendType.APPEND)
                }
                PostAppendFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
            mBinding.avatar -> {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, userId)
                }
                startActivity(intent)
            }
            mBinding.rewardInfoBtn -> {
                startActivity(Intent(this, CreditHistoryActivity::class.java))
            }
            mBinding.warningLayout -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                    putInt(Constant.IntentKey.USER_ID, userId)
                    putString(Constant.IntentKey.TYPE, BaseVBFragmentForBottom.BIZ_VIEW_WARNING)
                }
                BaseVBFragmentForBottom.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
        }
    }

    override fun onOptionsSelected(item: MenuItem?) {
        when(item?.itemId) {
            R.id.capture_content -> {
                if (!SharePrefUtil.isLogin(this)) {
                    return
                }
                if (CommonUtil.contains(Constant.SECURE_BOARD_ID, boardId)) {
                    showToast("该板块不允许截图", ToastType.TYPE_ERROR)
                    return
                }
                thread {
                    val bitmap = Bitmap.createBitmap(mBinding.headView.width, mBinding.headView.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(ColorUtil.getAttrColor(this, R.attr.colorSurface))
                    mBinding.headView.draw(canvas)

                    val a = try {
                        ImageUtil.setWaterMark("UID:".plus(SharePrefUtil.getUid(this).toString()), bitmap)
                    } catch (e: Exception) {
                        bitmap
                    }

                    val success = ImageUtil.saveBitmapToGallery(this, a, "uestebbs")
                    runOnUiThread {
                        if (success) {
                            showToast("成功保存到相册：Pictures/uestcbbs", ToastType.TYPE_SUCCESS)
                        } else {
                            showToast("保存失败", ToastType.TYPE_ERROR)
                        }
                    }
                }
            }
            R.id.copy_link -> {
                ClipBoardUtil.copyToClipBoard(this, postDetailBean?.forumTopicUrl)
            }
            R.id.open_link -> {
                CommonUtil.openBrowser(this, "http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=$topicId")
            }
            R.id.delete -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.POST_ID, postId)
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                }
                UseRegretMagicFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
            R.id.modify -> {
                val intent = Intent(this, WebViewActivity::class.java).apply {
                    putExtra(Constant.IntentKey.URL, "https://bbs.uestc.edu.cn/forum.php?mod=post&action=edit&tid=$topicId&pid=$postId")
                }
                startActivity(intent)
            }
            R.id.add_to_collection -> {
                val bundle = Bundle().apply {
                    putInt(Constant.IntentKey.TOPIC_ID, topicId)
                }
                AddToCollectionFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
            R.id.report -> {
                val bundle = Bundle().apply {
                    putString(Constant.IntentKey.TYPE, "thread")
                    putInt(Constant.IntentKey.ID, topicId)
                }
                ReportFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
            R.id.jump_to_comment -> {
                if (item.title == getString(R.string.post_detail_jump_to_top)) {
                    mBinding.scrollLayout.scrollTo(0, 0)
                } else if (item.title == getString(R.string.post_detail_jump_to_comment)) {
                    mBinding.scrollLayout.scrollTo(0, mBinding.scrollLayout.topScrollHeight)
                }
            }
            R.id.search_site -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            R.id.jump_to_floor -> {
                InputAlertDialogBuilder(getContext())
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setPositiveButton("确认", InputAlertDialogBuilder.OnPositiveListener { dialog, inputText ->
                        if (inputText.isNullOrEmpty()) {
                            showToast("请输入内容", ToastType.TYPE_ERROR)
                        } else {
                            dialog?.dismiss()
                            EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.LOCATE_COMMENT, NumberUtil.parseInt(inputText)))
                        }
                    })
                    .setMessage("请输入楼层序号或者帖子pid")
                    .setTitle("定位评论")
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
    }

    override fun setOnItemClickListener() {
        postCollectionAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(getContext(), CollectionDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.COLLECTION_ID, postCollectionAdapter.data[position].ctid)
            }
            startActivity(intent)
        }
        postDianPingAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.avatar) {
                val intent = Intent(getContext(), UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, postDianPingAdapter.data[position].uid)
                }
                startActivity(intent)
            }
        }
    }

    override fun onGetPostDetailSuccess(postDetailBean: PostDetailBean) {
        this.postId = postDetailBean.topic.reply_posts_id
        this.boardId = postDetailBean.boardId
        this.userId = postDetailBean.topic.user_id
        this.postDetailBean = postDetailBean

        if (CommonUtil.contains(Constant.SECURE_BOARD_ID, postDetailBean.boardId)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        mPresenter?.saveHistory(postDetailBean)

        if (SharePrefUtil.getUid(this) == postDetailBean.topic.user_id) {
            mBinding.toolbar.menu.findItem(R.id.delete)?.isVisible = true
            mBinding.toolbar.menu.findItem(R.id.modify)?.isVisible = true
            mBinding.toolbar.menu.findItem(R.id.report)?.isVisible = false
        } else {
            mBinding.toolbar.menu.findItem(R.id.delete)?.isVisible = false
            mBinding.toolbar.menu.findItem(R.id.modify)?.isVisible = false
            mBinding.toolbar.menu.findItem(R.id.report)?.isVisible = true
        }

        val title2 = if (postDetailBean.total_num != 0) "评论(${postDetailBean.total_num})" else "评论"
        val title4 = postDetailBean.topic?.reward?.userList?.size?.let { "评分(${it})" } ?: "评分"
        val titles = arrayOf("评价", title2, "点评", title4)
        mBinding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = NewPostDetailPagerAdapter(this@NewPostDetailActivity,
                topicId, postId, userId, boardId, postDetailBean.total_num, locateComment)
            desensitize()
        }

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        mBinding.viewpager.setCurrentItem(1, false)

        postContentAdapter = PostContentAdapter(this, topicId,
            onVoteClick = {
                mPresenter?.vote(topicId, postDetailBean.boardId, it)
            }
        )
        postContentAdapter.type = PostContentAdapter.TYPE.TOPIC
        mBinding.contentRv.adapter = postContentAdapter

        val data = JsonUtil.modelListA2B(postDetailBean.topic.content,
            ContentViewBean::class.java, postDetailBean.topic.content.size
        )
        postDetailBean.topic.poll_info?.let { poll ->
            data.add(ContentViewBean().apply {
                type = ContentDataType.TYPE_VOTE
                mPollInfoBean = poll
            })
        }
        postContentAdapter.data = data

        if (userId == 0 && "匿名" == postDetailBean.topic.user_nick_name) {
            mBinding.avatar.load(R.drawable.ic_anonymous)
        } else {
            mBinding.avatar.load(postDetailBean.topic.icon)
        }
        mBinding.title.text = postDetailBean.topic.title
        mBinding.name.text = postDetailBean.topic.user_nick_name
        mBinding.time.text = TimeUtil.formatTime(postDetailBean.topic.create_date, R.string.post_time1, this)
            .plus(" ").plus(if (TextUtils.isEmpty(postDetailBean.topic.mobileSign)) "网页版" else postDetailBean.topic.mobileSign.replace("来自", ""))
        mBinding.collectBtn.setImageResource(if (postDetailBean.topic.is_favor == 1) R.drawable.ic_star_fill_1 else R.drawable.ic_star_outline)

        if (postDetailBean.topic.user_id == SharePrefUtil.getUid(this)) {
            mBinding.pingfenBtn.visibility = View.GONE
            mBinding.buchongBtn.visibility = View.VISIBLE
            mBinding.line2.visibility = View.GONE
        } else {
            mBinding.pingfenBtn.visibility = View.VISIBLE
            mBinding.buchongBtn.visibility = View.GONE
            mBinding.line1.visibility = View.GONE
        }

        if (!postDetailBean.topic.userTitle.isNullOrEmpty()) {
            mBinding.level.visibility = View.VISIBLE
            val matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(postDetailBean.topic.userTitle)
            mBinding.level.apply {
                backgroundTintList = ColorStateList.valueOf(ForumUtil.getLevelColor(context, postDetailBean.topic.userTitle))
                setBackgroundResource(R.drawable.shape_post_detail_user_level)
                text = if (matcher.find()) {
                    if (matcher.group(2)?.contains("禁言") == true) { "禁言中" } else { matcher.group(2) }
                } else {
                    postDetailBean.topic.userTitle
                }
            }
        } else {
            mBinding.level.visibility = View.GONE
        }

        if (postDetailBean.postWebBean != null) {
            onGetPostWebDetailSuccess(postDetailBean.postWebBean)
        }

        mBinding.scrollLayout.post {
            setReadProgress()
        }

        mBinding.statusView.success()
    }

    override fun onGetPostDetailError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    private fun onGetPostWebDetailSuccess(postWebBean: PostWebBean) {
        SharePrefUtil.setForumHash(this, postWebBean.formHash)
        postDetailBean?.topic?.favoriteNum = NumberUtil.parseInt(postWebBean.favoriteNum)
        if (!postWebBean.actionHistory.isNullOrEmpty()) {
            mBinding.actionHistoryText.text = postWebBean.actionHistory
            mBinding.actionHistoryText.visibility = View.VISIBLE
        }

        if (postWebBean.rewardInfo.isNotEmpty() && postWebBean.shengYuReword.isNotEmpty()) {
            mBinding.rewardInfoLayout.visibility = View.VISIBLE

            if (postWebBean.shengYuReword.contains("水滴")) {
                mBinding.rewardInfoDsp.text = postWebBean.rewardInfo.plus("\n").plus("当前剩余：${postWebBean.shengYuReword}")
            } else {
                mBinding.rewardInfoDsp.text = postWebBean.rewardInfo
            }
        } else {
            mBinding.rewardInfoLayout.visibility = View.GONE
        }

        mBinding.collectNum.text = "${postWebBean.favoriteNum}收藏"
        mBinding.voteView.setNum(postWebBean.againstCount, postWebBean.supportCount)
        mBinding.voteView.setProgress(1f)
        mBinding.supportText.text = "${postWebBean.supportCount}人"
        mBinding.againstText.text = "${postWebBean.againstCount}人"

        if (postWebBean.collectionList.size == 0) {
            mBinding.collectionLayout.visibility = View.GONE
        } else {
            mBinding.collectionLayout.visibility = View.VISIBLE
            mBinding.collectionRv.adapter = postCollectionAdapter.apply {
                setNewData(postWebBean.collectionList)
            }
        }

        pingjiaCount = postWebBean.supportCount + postWebBean.againstCount
        mBinding.tabLayout.getTabAt(0)?.apply {
            text = if (pingjiaCount == 0) "评价" else "评价(${pingjiaCount})"
        }

        mBinding.stampImg.apply {
            if (postWebBean.originalCreate) {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pic_original_create))
            } else if (postWebBean.essence) {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pic_essence))
            } else if (postWebBean.topStick) {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pic_topstick))
            }
        }

        if (postWebBean.isWarned) {
            mBinding.warningLayout.visibility = View.VISIBLE
        } else {
            mBinding.warningLayout.visibility = View.GONE
        }

        mBinding.tabLayout.getTabAt(2)?.apply {
            text = if (postWebBean.dianPingBean.list.isNullOrEmpty()) {
                "点评"
            } else {
                if (postWebBean.dianPingBean.hasNext) {
                    "点评(${postWebBean.dianPingBean.list.size}+)"
                } else {
                    "点评(${postWebBean.dianPingBean.list.size})"
                }
            }
        }
        if (!postWebBean.dianPingBean.list.isNullOrEmpty()) {
            mBinding.dianpingLayout.visibility = View.VISIBLE
            mBinding.dianpingRv.adapter = postDianPingAdapter
            postDianPingAdapter.addData(postWebBean.dianPingBean.list, true)
        }
    }

    override fun onVoteSuccess(voteResultBean: VoteResultBean) {
        val last = postContentAdapter.mData.last()
        if (last.type == ContentDataType.TYPE_VOTE) {
            last.mPollInfoBean?.poll_status = 1
            voteResultBean.vote_rs?.forEach { rs ->
                last.mPollInfoBean?.poll_item_list?.forEach { origin ->
                    if (rs.name == origin.name) {
                        origin.total_num = rs.totalNum
                    }
                }
            }
            postContentAdapter.notifyItemChanged(postContentAdapter.mData.size - 1)
        }
    }

    override fun onVoteError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onFavoritePostSuccess(favoritePostResultBean: FavoritePostResultBean) {
        if (postDetailBean?.topic?.is_favor == 1) {
            showToast("取消收藏成功", ToastType.TYPE_SUCCESS)
            mBinding.collectBtn.setImageResource(R.drawable.ic_star_outline)
            postDetailBean?.topic?.is_favor = 0
        } else {
            showToast("收藏成功", ToastType.TYPE_SUCCESS)
            mBinding.collectBtn.setImageResource(R.drawable.ic_star_fill_1)
            postDetailBean?.topic?.is_favor = 1
            postDetailBean?.topic?.favoriteNum = (postDetailBean?.topic?.favoriteNum?:0) + 1
            mBinding.collectNum.text = "${postDetailBean?.topic?.favoriteNum}收藏"
        }
    }

    override fun onFavoritePostError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, type: String) {
        if (action == "support") {
            if (type == "thread") {
                mBinding.supportText.text = "${mBinding.voteView.getRightNum() + 1}人"
                mBinding.voteView.plusNum(0, 1)
            }
            showToast("点赞成功", ToastType.TYPE_SUCCESS)
        } else {
            if (type == "thread") {
                mBinding.againstText.text = "${mBinding.voteView.getLeftNum() + 1}人"
                mBinding.voteView.plusNum(1, 0)
            }
            showToast("点踩成功", ToastType.TYPE_SUCCESS)
        }

        pingjiaCount ++
        mBinding.tabLayout.getTabAt(0)?.apply {
            text = "评价(${pingjiaCount})"
        }
    }

    override fun onSupportError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        when(baseEvent.eventCode) {
            BaseEvent.EventCode.COMMENT_FRAGMENT_SCROLL -> {
                doBottomLayoutAnim(baseEvent.eventData as Int)
            }
            BaseEvent.EventCode.SCROLL_POST_DETAIL_TAB_TO_TOP -> {
                if (baseEvent.eventData == topicId) {
                    mBinding.scrollLayout.post{
                        mBinding.scrollLayout.scrollTo(0, mBinding.tabLayout.top)
                    }
                }
            }
            BaseEvent.EventCode.COMMENT_SORT_CHANGE -> {
                currentSort = baseEvent.eventData as CommentFragment.SORT
            }
            BaseEvent.EventCode.SEND_COMMENT_SUCCESS -> {
                mBinding.tabLayout.getTabAt(1)?.apply {
                    text = "评论".plus("(").plus((postDetailBean?.total_num?:0) + 1).plus(")")
                }
            }
            BaseEvent.EventCode.COMMENT_REFRESHED -> {
                val event = baseEvent.eventData as CommentRefreshEvent
                if (topicId == event.topicId) {
                    mBinding.tabLayout.getTabAt(1)?.apply {
                        text = "评论".plus("(").plus(if (event.commentNum == 0) 0 else event.commentNum).plus(")")
                    }
                }
            }
        }
    }

    private fun doBottomLayoutAnim(dy: Int) {
        if (dy < 0 && mBinding.bottomLayout.visibility == View.GONE) {
            mBinding.bottomLayout.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_appear_y1_y0_no_alpha))
            }
        }
        if (dy > 0 && mBinding.bottomLayout.visibility == View.VISIBLE) {
            mBinding.bottomLayout.apply {
                visibility = View.GONE
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_dismiss_y0_y1_no_alpha))
            }
        }
    }

    private fun setReadProgress(scrollY: Int = 0) {
        if (mBinding.scrollLayout.topScrollHeight > ScreenUtil.getScreenHeight(this)) {
            mBinding.readProgress.visibility = View.VISIBLE
            mBinding.toolbar.menu?.findItem(R.id.jump_to_comment)?.apply {
                isVisible = true
                title = getString(R.string.post_detail_jump_to_comment)
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_down_double_arrow, theme)
            }
            mBinding.readProgress.progress = ((scrollY.toFloat() /
                    (mBinding.scrollLayout.topScrollHeight - ScreenUtil.getScreenHeight(this)).toFloat())
                    * mBinding.readProgress.max).toInt()
            if (mBinding.readProgress.progress == mBinding.readProgress.max) {
                mBinding.readProgress.visibility = View.INVISIBLE
                mBinding.toolbar.menu?.findItem(R.id.jump_to_comment)?.apply {
                    isVisible = true
                    title = getString(R.string.post_detail_jump_to_top)
                    icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_up_double_arrow, theme)
                }
            }
        } else {
            mBinding.readProgress.visibility = View.INVISIBLE
            mBinding.toolbar.menu?.findItem(R.id.jump_to_comment)?.apply {
                isVisible = false
            }
        }
    }

    override fun onNestScrolling(dy: Int, scrollY: Int) {
        doBottomLayoutAnim(dy)
        setReadProgress(scrollY)
    }

    override fun onBackPressed() {
        finish()
    }

    private val mPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {

        }
    }
}