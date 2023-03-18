package com.scatl.uestcbbs.module.post.view

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.PostAppendType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentCommentBinding
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SupportResultBean
import com.scatl.uestcbbs.module.magic.view.UseRegretMagicFragment
import com.scatl.uestcbbs.module.post.adapter.PostCommentAdapter
import com.scatl.uestcbbs.module.post.presenter.CommentPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.showToast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

class CommentFragment : BaseVBFragment<CommentPresenter, CommentView, FragmentCommentBinding>(), CommentView {

    private var page = 1
    private var topicId = 0
    private var order = 0
    private var topicAuthorId = 0
    private var boardId = 0
    private var sortAuthorId = 0 //排序用的楼主id
    private var currentSort = SORT.DEFAULT
    private lateinit var commentAdapter: PostCommentAdapter
    private var totalCommentData = mutableListOf<PostDetailBean.ListBean>()

    private enum class SORT {
        DEFAULT, NEW, AUTHOR, FLOOR
    }

    companion object {
        const val PAGE_SIZE = 2000
        fun getInstance(bundle: Bundle?) = CommentFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.run {
            topicId = getInt(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
            topicAuthorId = getInt(Constant.IntentKey.USER_ID, Int.MAX_VALUE)
            boardId = getInt(Constant.IntentKey.BOARD_ID, Int.MAX_VALUE)
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
            if (view.id == R.id.item_post_comment_reply_button || view.id == R.id.item_post_comment_root_rl) {
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
            if (view.id == R.id.item_post_comment_support_button) {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mPresenter?.support(topicId, commentAdapter.data[position].reply_posts_id, "post", "support", position)
            }
            if (view.id == R.id.item_post_comment_author_avatar) {
                val intent = Intent(context, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, commentAdapter.data[position].reply_id)
                }
                startActivity(intent)
            }
            if (view.id == R.id.item_post_comment_more_button) {
                mPresenter?.moreReplyOptionsDialog(boardId, topicId, topicAuthorId, commentAdapter.data[position])
            }
            if (view.id == R.id.quote_layout) {
                val pid = commentAdapter.data[position].quote_pid
                val data: PostDetailBean.ListBean? = mPresenter?.findCommentByPid(totalCommentData, pid)
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
            if (view.id == R.id.item_post_comment_root_rl) {
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
            mPresenter?.getPostComment(page, PAGE_SIZE, order, topicId, sortAuthorId)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getPostComment(page, PAGE_SIZE, order, topicId, sortAuthorId)
    }

    override fun onGetPostCommentSuccess(postDetailBean: PostDetailBean) {
        page += 1
        mBinding.statusView.success()

        if (postDetailBean.has_next == 1 && currentSort != SORT.FLOOR) {
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }

        commentAdapter.setAuthorId(postDetailBean.topic.user_id)

        if (postDetailBean.page == 1) {
            mBinding.recyclerView.scheduleLayoutAnimation()
            when (currentSort) {
                SORT.DEFAULT -> {
                    totalCommentData = postDetailBean.list
                    commentAdapter.setTotalCommentData(totalCommentData)
                    commentAdapter.setNewData(mPresenter?.resortComment(postDetailBean))
                }
                SORT.FLOOR -> {
                    mPresenter?.getFloorInFloorCommentData(postDetailBean)
                }
                SORT.AUTHOR -> {
                    commentAdapter.setNewData(postDetailBean.list)
                }
                SORT.NEW -> {
                    totalCommentData = postDetailBean.list
                    commentAdapter.setTotalCommentData(totalCommentData)
                    commentAdapter.setNewData(postDetailBean.list)
                }
            }
        } else {
            commentAdapter.addData(postDetailBean.list)
        }

        if (postDetailBean.list == null || postDetailBean.list.size == 0) {
            mBinding.statusView.error("还没有评论")
        }
    }

    override fun onGetPostCommentError(msg: String?, code: Int) {

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
            commentAdapter.refreshNotifyItemChanged(position, PostCommentAdapter.Payload.UPDATE_SUPPORT)
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
        }
        ViewDaShangFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
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
            putString(Constant.IntentKey.TYPE, PostAppendType.DIANPING)
        }
        ViewDianPingFragment.getInstance(bundle).show(childFragmentManager, TimeUtil.getStringMs())
    }

    override fun onGetReplyDataSuccess(postDetailBean: PostDetailBean, replyPosition: Int) {
        if (postDetailBean.list != null) {
            for (data in postDetailBean.list) {
                if (data.reply_id == SharePrefUtil.getUid(context) && this::commentAdapter.isInitialized) {
                    try {
                        totalCommentData.add(data)
                        if (replyPosition == -1) {
                            val insertPosition = (mBinding.recyclerView.layoutManager as LinearLayoutManager)
                                .findFirstCompletelyVisibleItemPosition()
                            commentAdapter.data.add(insertPosition + 1, data)
                            commentAdapter.notifyItemInserted(insertPosition + 1)
                            mBinding.statusView.success()
                        } else {
                            commentAdapter.data.add(replyPosition + 1, data)
                            commentAdapter.notifyItemInserted(replyPosition + 1)
                            (mBinding.recyclerView.layoutManager as LinearLayoutManager)
                                .scrollToPositionWithOffset(replyPosition + 1, 0)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    break
                }
            }
        }
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SEND_COMMENT_SUCCESS) {
            mPresenter?.getReplyData(topicId, baseEvent.eventData as Int)
        }
    }

}