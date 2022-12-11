package com.scatl.uestcbbs.module.post.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ContentDataType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseActivity
import com.scatl.uestcbbs.databinding.ActivityNewPostDetailBinding
import com.scatl.uestcbbs.entity.*
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common
import com.scatl.uestcbbs.module.collection.view.CollectionActivity
import com.scatl.uestcbbs.module.post.adapter.NewPostDetailPagerAdapter
import com.scatl.uestcbbs.module.post.adapter.PostCollectionAdapter
import com.scatl.uestcbbs.module.post.adapter.PostContentAdapter
import com.scatl.uestcbbs.module.post.presenter.NewPostDetailPresenter
import com.scatl.uestcbbs.util.*

@SuppressLint("SetTextI18n")
class NewPostDetailActivity : BaseActivity<NewPostDetailPresenter>(), NewPostDetailView {
    private lateinit var binding: ActivityNewPostDetailBinding
    private var topicId: Int = Int.MAX_VALUE
    private lateinit var postDetailBean: PostDetailBean
    private lateinit var postContentAdapter: PostContentAdapter
    private lateinit var postCollectionAdapter: PostCollectionAdapter

    override fun setLayoutRootView(): View {
        binding = ActivityNewPostDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun findView() {

    }

    override fun getIntent(intent: Intent) {
        super.getIntent(intent)
        topicId = intent.getIntExtra(Constant.IntentKey.TOPIC_ID, Int.MAX_VALUE)
    }

    override fun initView() {
        super.initView()
        binding.scrollLayout.apply {
            setTopView(binding.headView)
            setContentView(binding.viewpager)
            setInnerOffsetView(binding.tabLayout)
        }
        postCollectionAdapter = PostCollectionAdapter(R.layout.item_post_detail_collection)

        binding.collectBtn.setOnClickListener(this)
        binding.supportBtn.setOnClickListener(this)
        binding.againstBtn.setOnClickListener(this)
        presenter.getPostDetail(1, 0, 0, topicId, 0)
    }

    override fun initPresenter() = NewPostDetailPresenter()

    override fun getContext(): Context {
        return this
    }

    override fun onClickListener(view: View?) {
        when(view) {
            binding.collectBtn -> {
                showToast("操作中，请稍候...", ToastType.TYPE_NORMAL)
                presenter.favorite("tid", if (postDetailBean.topic.is_favor == 1) "delfavorite" else "favorite",
                    postDetailBean.topic.topic_id)
            }
            binding.supportBtn -> {
                presenter.support(topicId, postDetailBean.topic.reply_posts_id, "thread", "support")
            }
            binding.againstBtn -> {
                presenter.support(topicId, postDetailBean.topic.reply_posts_id, "thread", "against")
            }
        }
    }

    override fun onOptionsSelected(item: MenuItem) {
        if (item.itemId == R.id.capture_content) {
            val bitmap = Bitmap.createBitmap(binding.headView.width, binding.headView.height, Bitmap.Config.ARGB_8888)
            val c = Canvas(bitmap).apply {
                drawColor(binding.headView.solidColor)
            }
            binding.headView.draw(c)
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "1111", "")
        }
    }

    override fun setOnItemClickListener() {
        postCollectionAdapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val intent = Intent(this@NewPostDetailActivity, CollectionActivity::class.java)
            intent.putExtra(Constant.IntentKey.COLLECTION_ID, postCollectionAdapter.data[position].ctid)
            startActivity(intent)
        }
    }

    override fun onGetPostDetailSuccess(postDetailBean: PostDetailBean) {
        this.postDetailBean = postDetailBean
        presenter.getPostWebDetail(topicId, 1)

        val title2 = if (postDetailBean.total_num != 0) "评论(${postDetailBean.total_num})" else "评论"
        val title4 = postDetailBean.topic?.reward?.userList?.size?.let { "评分(${it})" } ?: "评分"
        val titles = arrayOf("点赞", title2, "点评", title4)
        binding.viewpager.apply {
            offscreenPageLimit = titles.size
            adapter = NewPostDetailPagerAdapter(this@NewPostDetailActivity,
                topicId, postDetailBean.topic?.reply_posts_id?:-1)
            desensitize()
        }

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        binding.viewpager.setCurrentItem(1, false)

        postContentAdapter = PostContentAdapter(this,
            onVoteClick = {
                presenter.vote(topicId, postDetailBean.boardId, it)
            }
        )
        binding.contentRv.adapter = postContentAdapter
        postContentAdapter.data = postDetailBean.topic

        GlideLoader4Common.simpleLoad(this, postDetailBean.topic.icon, binding.avatar)
        binding.title.text = postDetailBean.topic.title
        binding.name.text = postDetailBean.topic.user_nick_name
        binding.time.text = TimeUtil.formatTime(postDetailBean.topic.create_date, R.string.post_time1, this)
            .plus(" ").plus(if (TextUtils.isEmpty(postDetailBean.topic.mobileSign)) "来自网页版" else postDetailBean.topic.mobileSign)
        binding.collectBtn.setImageResource(if (postDetailBean.topic.is_favor == 1) R.drawable.ic_star_fill_1 else R.drawable.ic_star_outline)
    }

    override fun onGetPostDetailError(msg: String?) {
        
    }

    override fun onGetPostWebDetailSuccess(postWebBean: PostWebBean) {
        SharePrefUtil.setForumHash(this, postWebBean.formHash)
        binding.tabLayout.getTabAt(0)?.apply {
            text = if (postWebBean.supportCount == 0) "点赞" else "点赞(${postWebBean.supportCount})"
        }
        postDetailBean.topic.favoriteNum = NumberUtil.parseInt(postWebBean.favoriteNum)
        if (!postWebBean.actionHistory.isNullOrEmpty()) {
            binding.actionHistoryText.text = postWebBean.actionHistory
            binding.actionHistoryCard.visibility = View.VISIBLE
        }
        binding.collectNum.text = "${postWebBean.favoriteNum}收藏"
        binding.voteView.setNum(postWebBean.againstCount, postWebBean.supportCount)
        binding.supportText.text = "${postWebBean.supportCount}人"
        binding.againstText.text = "${postWebBean.againstCount}人"

        if (postWebBean.collectionList.size == 0) {
            binding.collectionLayout.visibility = View.GONE
        } else {
            binding.collectionLayout.visibility = View.VISIBLE
            binding.collectionRv.adapter = postCollectionAdapter.apply {
                setNewData(postWebBean.collectionList)
            }
        }

        binding.stampImg.apply {
            if (postWebBean.originalCreate) {
                setImageDrawable(ContextCompat.getDrawable(this@NewPostDetailActivity, R.drawable.pic_original_create))
            } else if (postWebBean.essence) {
                setImageDrawable(ContextCompat.getDrawable(this@NewPostDetailActivity, R.drawable.pic_essence))
            } else if (postWebBean.topStick) {
                setImageDrawable(ContextCompat.getDrawable(this@NewPostDetailActivity, R.drawable.pic_topstick))
            }
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

//{
//	"rs": 1,
//	"errcode": "\u6295\u7968\u6210\u529f",
//	"head": {
//		"errCode": "00000000",
//		"errInfo": "\u6295\u7968\u6210\u529f",
//		"version": "2.6.1.7",
//		"alert": 1
//	},
//	"body": {
//		"externInfo": {
//			"padding": ""
//		}
//	},
//	"vote_rs": [{
//		"name": "\u56de",
//		"pollItemId": 28307,
//		"totalNum": 90
//	}, {
//		"name": "\u4e0d\u56de",
//		"pollItemId": 28308,
//		"totalNum": 38
//	}, {
//		"name": "\u60f3\u56de\uff0c\u4e0d\u53ef\u6297\u529b\u4e0d\u80fd\u56de",
//		"pollItemId": 28309,
//		"totalNum": 54
//	}, {
//		"name": "\u6c34\u6c34",
//		"pollItemId": 28310,
//		"totalNum": 83
//	}]
//}
    }

    override fun onVoteError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onFavoritePostSuccess(favoritePostResultBean: FavoritePostResultBean) {
        if (postDetailBean.topic.is_favor == 1) {
            showToast("取消收藏成功", ToastType.TYPE_SUCCESS)
            binding.collectBtn.setImageResource(R.drawable.ic_post_detail_not_favorite)
            postDetailBean.topic.is_favor = 0
        } else {
            showToast("收藏成功", ToastType.TYPE_SUCCESS)
            binding.collectBtn.setImageResource(R.drawable.ic_post_detail_favorite)
            postDetailBean.topic.is_favor = 1
            postDetailBean.topic.favoriteNum ++
            binding.collectNum.text = "${postDetailBean.topic.favoriteNum}收藏"
        }
    }

    override fun onFavoritePostError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onSupportSuccess(supportResultBean: SupportResultBean, action: String, type: String) {
        if (action == "support") {
            if (type == "thread") {
                binding.supportText.text = "${binding.voteView.rightNum + 1}人"
                binding.voteView.setNum(binding.voteView.leftNum, binding.voteView.rightNum + 1)
            }
            showToast("点赞成功", ToastType.TYPE_SUCCESS)
        } else {
            if (type == "thread") {
                binding.againstText.text = "${binding.voteView.leftNum + 1}人"
                binding.voteView.setNum(binding.voteView.leftNum + 1, binding.voteView.rightNum)
            }
            showToast("点踩成功", ToastType.TYPE_SUCCESS)
        }
    }

    override fun onSupportError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }
}