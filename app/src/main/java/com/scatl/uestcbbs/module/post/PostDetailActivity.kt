package com.scatl.uestcbbs.module.post

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ContentDataType
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityPostDetailBinding
import com.scatl.uestcbbs.entity.ContentViewBean
import com.scatl.uestcbbs.entity.FavoritePostResultBean
import com.scatl.uestcbbs.entity.PostDetailBean
import com.scatl.uestcbbs.entity.SupportResultBean
import com.scatl.uestcbbs.entity.VoteResultBean
import com.scatl.uestcbbs.module.post.adapter.DianPingAdapter
import com.scatl.uestcbbs.module.post.adapter.PostCollectionAdapter
import com.scatl.uestcbbs.module.post.adapter.PostContentAdapter
import com.scatl.uestcbbs.module.post.presenter.NewPostDetailPresenter
import com.scatl.uestcbbs.module.post.view.CommentFragment
import com.scatl.uestcbbs.module.post.view.NewPostDetailView
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.JsonUtil
import com.scatl.util.ScreenUtil

/**
 * Created by sca_tl at 2023/6/7 20:03
 */
class PostDetailActivity: BaseVBActivity<NewPostDetailPresenter, NewPostDetailView, ActivityPostDetailBinding>(), NewPostDetailView {

    private var topicId: Int = Int.MAX_VALUE
    private var postId: Int = Int.MAX_VALUE
    private var boardId: Int = Int.MAX_VALUE
    private var userId: Int = Int.MAX_VALUE
    private var locateComment: Bundle? = null
    private var pingjiaCount: Int = 0
    private var currentSort = CommentFragment.SORT.DEFAULT
    private var postDetailBean: PostDetailBean? = null
    private lateinit var postDetailAdapter: PostDetailAdapter

    override fun getIntent(intent: Intent?) {
        intent?.let {
            topicId = it.getIntExtra(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
            locateComment = it.getBundleExtra(Constant.IntentKey.LOCATE_COMMENT)
        }
    }

    override fun getViewBinding() = ActivityPostDetailBinding.inflate(layoutInflater)

    override fun initPresenter() = NewPostDetailPresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(true)

        mPresenter?.getDetail(1, 0, 0, topicId, 0)
    }

    override fun onGetPostDetailSuccess(postDetailBean: PostDetailBean) {
        val detailEntity = mutableListOf<MultiItemEntity>()

        //头部数据
        detailEntity.add(postDetailBean)

        //帖子内容数据
        detailEntity.addAll(postDetailBean.topic.content)

        //投票
        postDetailBean.topic.poll_info?.let { poll ->
            detailEntity.add(poll)
        }

        //点赞，点踩，专辑，点评
        detailEntity.add(PostDetailAdapter.ExtraEntity())

        //tab + viewpager
        detailEntity.add(PostDetailAdapter.ViewPagerEntity())

        postDetailAdapter = PostDetailAdapter(getContext(), detailEntity)
        postDetailAdapter.postDetailBean = postDetailBean
        mBinding.recyclerView.adapter = postDetailAdapter
    }

    override fun onGetPostDetailError(msg: String?) {

    }

    override fun onVoteSuccess(voteResultBean: VoteResultBean) {

    }

    override fun onVoteError(msg: String?) {

    }

    override fun onFavoritePostSuccess(favoritePostResultBean: FavoritePostResultBean) {

    }

    override fun onFavoritePostError(msg: String?) {

    }

    override fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, type: String) {

    }

    override fun onSupportError(msg: String?) {

    }

    override fun getContext() = this
}