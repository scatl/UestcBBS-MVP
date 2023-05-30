package com.scatl.uestcbbs.module.post.view

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.PostAppendType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.base.BaseVBFragmentForBottom
import com.scatl.uestcbbs.databinding.FragmentCommentBinding
import com.scatl.uestcbbs.entity.CommentRefreshEvent
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SendCommentSuccessEntity
import com.scatl.uestcbbs.entity.SupportResultBean
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter
import com.scatl.uestcbbs.module.post.presenter.CommentPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.CommentUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.dialog.BlurAlertDialogBuilder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

class CommentFragment : BaseVBFragment<CommentPresenter, CommentView, FragmentCommentBinding>(), CommentView {

    private var page = 1
    private var count = 0
    private var topicId = 0
    private var order = 0
    private var topicAuthorId = 0
    private var boardId = 0
    private var sortAuthorId = 0 //排序用的楼主id
    private var locatedPid = 0
    private var viewDianPing = false
    private var currentSort = SORT.DEFAULT
    private lateinit var commentAdapter: PostCommentAdapter
    private var totalCommentData = mutableListOf<PostDetailBean.ListBean>()

    enum class SORT {
        DEFAULT, NEW, AUTHOR, FLOOR
    }

    companion object {
        const val PAGE_SIZE = 500
        fun getInstance(bundle: Bundle?) = CommentFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.run {
            topicId = getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
            topicAuthorId = getInt(Constant.IntentKey.USER_ID, Int.MAX_VALUE)
            boardId = getInt(Constant.IntentKey.BOARD_ID, Int.MAX_VALUE)
            count = getInt(Constant.IntentKey.COUNT)

            getBundle(Constant.IntentKey.LOCATE_COMMENT)?.let {
                locatedPid = it.getInt(Constant.IntentKey.POST_ID, Int.MAX_VALUE)
                viewDianPing = it.getBoolean(Constant.IntentKey.VIEW_DIANPING, false)
            }
        }
    }

    override fun getViewBinding() = FragmentCommentBinding.inflate(layoutInflater)

    override fun initPresenter() = CommentPresenter()

    override fun initView() {
        super.initView()
        commentAdapter = PostCommentAdapter(R.layout.item_post_comment)
        mBinding.recyclerView.apply {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            adapter = commentAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.COMMENT_FRAGMENT_SCROLL, dy))
                }
            })
        }

        if (topicAuthorId == 0) {
            mBinding.authorSortBtn.visibility = View.GONE
        }

        mBinding.defaultSortBtn.setOnClickListener(this)
        mBinding.newSortBtn.setOnClickListener(this)
        mBinding.authorSortBtn.setOnClickListener(this)
        mBinding.floorSortBtn.setOnClickListener(this)
        mBinding.chipGroup.check(R.id.default_sort_btn)
        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getPostComment(page, PAGE_SIZE, order, topicId, sortAuthorId)
    }

    override fun setOnItemClickListener() {
        commentAdapter.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            if (view.id == R.id.btn_reply || view.id == R.id.root_layout) {
                val intent = Intent(context, CreateCommentActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, boardId)
                    putExtra(Constant.IntentKey.TOPIC_ID, topicId)
                    putExtra(Constant.IntentKey.QUOTE_ID, commentAdapter.data[position].reply_posts_id)
                    putExtra(Constant.IntentKey.IS_QUOTE, true)
                    putExtra(Constant.IntentKey.USER_NAME, commentAdapter.data[position].reply_name)
                    putExtra(Constant.IntentKey.POSITION, position)
                }
                startActivity(intent)
            }
            if (view.id == R.id.btn_support) {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mPresenter?.support(topicId, commentAdapter.data[position].reply_posts_id, "post", "support", position)
            }
            if (view.id == R.id.reply_avatar) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, commentAdapter.data[position].reply_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.btn_more) {
                mPresenter?.moreReplyOptionsDialog(boardId, topicId, topicAuthorId, commentAdapter.data[position])
            }
            if (view.id == R.id.quote_layout) {
                val pid = commentAdapter.data[position].quote_pid
                val data: PostDetailBean.ListBean? = CommentUtil.findCommentByPid(totalCommentData, pid)
                if (data != null) {
                    val bundle = Bundle().apply {
                        putInt(Constant.IntentKey.TOPIC_ID, topicId)
                        putSerializable(Constant.IntentKey.DATA_1, data)
                    }
                    if (context is FragmentActivity) {
                        ViewOriginCommentFragment
                            .getInstance(bundle)
                            .show((context as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                    }
                }
            }
        }

        commentAdapter.setOnItemChildLongClickListener { adapter: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            if (view.id == R.id.root_layout) {
                mPresenter?.moreReplyOptionsDialog(boardId, topicId, topicAuthorId, commentAdapter.data[position])
            }
            false
        }
    }

    override fun onClick(v: View) {
        if (v == mBinding.defaultSortBtn || v == mBinding.newSortBtn || v == mBinding.authorSortBtn || v == mBinding.floorSortBtn) {
            when (v) {
                mBinding.defaultSortBtn -> {
                    currentSort = SORT.DEFAULT
                    order = 0
                    sortAuthorId = 0
                }
                mBinding.newSortBtn -> {
                    currentSort = SORT.NEW
                    order = 1
                    sortAuthorId = 0
                }
                mBinding.authorSortBtn -> {
                    currentSort = SORT.AUTHOR
                    sortAuthorId = topicAuthorId
                }
                mBinding.floorSortBtn -> {
                    currentSort = SORT.FLOOR
                    order = 0
                    sortAuthorId = 0
                }
            }
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            page = 1
            mBinding.statusView.loading()
            commentAdapter.setNewData(ArrayList())
            mPresenter?.getPostComment(page, if (currentSort == SORT.FLOOR) 1000 else PAGE_SIZE, order, topicId, sortAuthorId)
            EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.COMMENT_SORT_CHANGE, currentSort))
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getPostComment(page, PAGE_SIZE, order, topicId, sortAuthorId)
    }

    override fun onGetPostCommentSuccess(postDetailBean: PostDetailBean) {
        mBinding.statusView.success()

        if (page == 1) {
            EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.COMMENT_REFRESHED,
                CommentRefreshEvent(postDetailBean.topic.topic_id, postDetailBean.total_num)))
            commentAdapter.authorId = postDetailBean.topic.user_id
            if (postDetailBean.list.isNullOrEmpty()) {
                mBinding.statusView.error("还没有评论")
            } else {
                mBinding.recyclerView.scheduleLayoutAnimation()
                when (currentSort) {
                    SORT.DEFAULT -> {
                        totalCommentData = postDetailBean.list
                        commentAdapter.totalCommentData = totalCommentData
                        commentAdapter.setNewData(CommentUtil.resortComment(postDetailBean))
                    }
                    SORT.FLOOR -> {
                        CommentUtil.getFloorInFloorCommentData(postDetailBean)
                    }
                    SORT.AUTHOR -> {
                        commentAdapter.addData(postDetailBean.list, true)
                    }
                    SORT.NEW -> {
                        totalCommentData = postDetailBean.list
                        commentAdapter.totalCommentData = totalCommentData
                        commentAdapter.addData(postDetailBean.list, true)
                    }
                }
            }
        } else {
            commentAdapter.addData(postDetailBean.list, false)
            totalCommentData.addAll(postDetailBean.list)
            commentAdapter.totalCommentData = totalCommentData
        }

        if (postDetailBean.has_next == 1) {
            page ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }

        jumpToCommentIfPossible(CommentUtil.getIndexByPid(commentAdapter.data, locatedPid.toString()), viewDianPing)
        locatedPid = Int.MAX_VALUE
        viewDianPing = false
    }

    private fun jumpToCommentIfPossible(position: Int?, viewDianPing: Boolean) {
        if (position == null || position < 0 || position > commentAdapter.data.size) {
            return
        }
        (mBinding.recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(position, ScreenUtil.dip2px(requireContext(), 150f))
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SCROLL_POST_DETAIL_TAB_TO_TOP, topicId))
        mBinding.recyclerView.postDelayed({
            val rootView = mBinding.recyclerView.layoutManager?.findViewByPosition(position)
            if (rootView != null) {
                val originBg = rootView.solidColor
                ValueAnimator
                    .ofArgb(
                        originBg,
                        ColorUtil.getAlphaColor(0.3f, ColorUtil.getAttrColor(context, R.attr.colorPrimary)),
                        originBg
                    )
                    .setDuration(1000)
                    .apply {
                        addUpdateListener {
                            rootView.setBackgroundColor(it.animatedValue as Int)
                        }
                        start()
                    }
            }
        }, 500)

        if (viewDianPing) {
            Handler(Looper.getMainLooper()).postDelayed({ onDianPing(commentAdapter.data[position].reply_posts_id) }, 2000)
        }
    }

    override fun onGetPostCommentError(msg: String?, code: Int) {
        if (page == 1) {
            if (commentAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onAppendPost(replyPostsId: Int, tid: Int) {
        val bundle = Bundle().apply {
            putInt(Constant.IntentKey.POST_ID, replyPostsId)
            putInt(Constant.IntentKey.TOPIC_ID, tid)
            putString(Constant.IntentKey.TYPE, PostAppendType.APPEND)
        }
        PostAppendFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
    }

    override fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, position: Int) {
        if (action == "support") {
            showToast(supportResultBean.head.errInfo, ToastType.TYPE_SUCCESS)
            commentAdapter.refreshNotifyItemChanged(position, PostCommentAdapter.UPDATE_SUPPORT)
        } else {
            showToast("踩+1", ToastType.TYPE_SUCCESS)
        }
    }

    override fun onSupportError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onPingFen(pid: Int) {
        val bundle = Bundle().apply {
            putInt(Constant.IntentKey.TOPIC_ID, topicId)
            putInt(Constant.IntentKey.POST_ID, pid)
            putString(Constant.IntentKey.TYPE, BaseVBFragmentForBottom.BIZ_PINGFEN)
        }
        BaseVBFragmentForBottom.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
    }

    override fun onOnlyReplyAuthor(uid: Int) {

    }

    override fun onDeletePost(tid: Int, pid: Int) {
        val bundle = Bundle().apply {
            putInt(Constant.IntentKey.POST_ID, pid)
            putInt(Constant.IntentKey.TOPIC_ID, tid)
        }
        UseRegretMagicFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
    }

    override fun onStickReplySuccess(msg: String?) {
        showToast(msg, ToastType.TYPE_SUCCESS)
        mBinding.recyclerView.scrollToPosition(0)
        mPresenter?.getPostComment(page, PAGE_SIZE, order, topicId, sortAuthorId)
    }

    override fun onStickReplyError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onDianPing(pid: Int) {
        val bundle = Bundle().apply {
            putInt(Constant.IntentKey.TOPIC_ID, topicId)
            putInt(Constant.IntentKey.POST_ID, pid)
            putString(Constant.IntentKey.TYPE, BaseVBFragmentForBottom.BIZ_DIANPING)
        }
        BaseVBFragmentForBottom.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
    }

    override fun onGetReplyDataSuccess(postDetailBean: PostDetailBean, replyPosition: Int, replyId: Int) {
        if (postDetailBean.list != null) {
            for (data in postDetailBean.list) {
                if (data.reply_id == replyId && this::commentAdapter.isInitialized) {
                    try {
                        totalCommentData.add(data)
                        val insertPosition: Int
                        if (replyPosition == -1) {
                            insertPosition = (mBinding.recyclerView.layoutManager as LinearLayoutManager)
                                .findFirstCompletelyVisibleItemPosition() + 1
                            commentAdapter.data.add(insertPosition, data)
                            commentAdapter.notifyItemInserted(insertPosition)
                            mBinding.statusView.success()
                        } else {
                            insertPosition = replyPosition + 1
                            commentAdapter.data.add(insertPosition, data)
                            commentAdapter.notifyItemInserted(insertPosition)
                            (mBinding.recyclerView.layoutManager as LinearLayoutManager)
                                .scrollToPositionWithOffset(insertPosition, 0)
                        }

                        mPresenter?.checkIfGetAward(topicId, data.reply_posts_id, insertPosition)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    break
                }
            }
        }
    }

    override fun onGetAwardInfoSuccess(info: String, commentPosition: Int) {
        val payload = Bundle().apply {
            putString("key", PostCommentAdapter.UPDATE_AWARD_INFO)
            putString("info", info)
        }
        commentAdapter.refreshNotifyItemChanged(commentPosition, payload)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_get_award, LinearLayout(context))
        val infoTv = view.findViewById<TextView>(R.id.info)
        infoTv.text = info
        BlurAlertDialogBuilder(requireContext())
            .setPositiveButton("好的😋", null)
            .setView(view)
            .create()
            .show()
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SEND_COMMENT_SUCCESS) {
            val successEntity = baseEvent.eventData as SendCommentSuccessEntity
            mPresenter?.getReplyData(topicId, successEntity.replyPosition, successEntity.replyId)
        } else if (baseEvent.eventCode == BaseEvent.EventCode.LOCATE_COMMENT) {
            val data = baseEvent.eventData as Int
            val positionByFloor = CommentUtil.getIndexByFloor(commentAdapter.data, data.toString())
            if (positionByFloor != null) {
                jumpToCommentIfPossible(positionByFloor, false)
            } else {
                val positionByPid = CommentUtil.getIndexByPid(commentAdapter.data, data.toString())
                if (positionByPid != null) {
                    jumpToCommentIfPossible(positionByPid, false)
                }
            }
        }
    }
}