package com.scatl.uestcbbs.module.collection.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.gyf.immersionbar.ImmersionBar
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityCollectionDetailBinding
import com.scatl.uestcbbs.entity.CollectionDetailBean
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.board.view.behavior.ContentBehavior
import com.scatl.uestcbbs.module.board.view.behavior.CoverBehavior
import com.scatl.uestcbbs.module.board.view.behavior.TitleBehavior
import com.scatl.uestcbbs.module.board.view.behavior.ToolbarBehavior
import com.scatl.uestcbbs.module.collection.adapter.CollectionPostAdapter
import com.scatl.uestcbbs.module.collection.adapter.CollectionSameOwnerAdapter
import com.scatl.uestcbbs.module.collection.adapter.CollectionTagAdapter
import com.scatl.uestcbbs.module.collection.presenter.CollectionDetailPresenter
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.isNullOrEmpty
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ImageUtil
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.dialog.BlurAlertDialogBuilder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

class CollectionDetailActivity : BaseVBActivity<CollectionDetailPresenter, CollectionDetailView, ActivityCollectionDetailBinding>(),
    CollectionDetailView, CoverBehavior.OnCoverViewChanged {

    private var mPage = 1
    private var cid = 0
    private var mUnRead = false
    private lateinit var collectionPostAdapter: CollectionPostAdapter
    private lateinit var sameOwnerAdapter: CollectionSameOwnerAdapter
    private lateinit var collectionDetailBean: CollectionDetailBean

    override fun getViewBinding() = ActivityCollectionDetailBinding.inflate(layoutInflater)

    override fun initPresenter() = CollectionDetailPresenter()

    override fun getIntent(intent: Intent?) {
        super.getIntent(intent)
        intent?.let {
            cid = it.getIntExtra(Constant.IntentKey.COLLECTION_ID, 0)
            mUnRead = it.getBooleanExtra(Constant.IntentKey.IS_NEW_CONTENT, false)
        }
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        initBehavior()

        sameOwnerAdapter = CollectionSameOwnerAdapter(R.layout.item_same_owner_collection)
        collectionPostAdapter = CollectionPostAdapter(R.layout.item_collection_post)
        mBinding.recyclerView.apply {
            adapter = collectionPostAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
        }

        bindClickEvent(mBinding.avatar, mBinding.subscribe)

        mBinding.statusView.loading(mBinding.coverLayout, mBinding.appBar, mBinding.contentLayout)

        mPresenter?.getCollectionDetail(cid, 1)
    }

    private fun initBehavior() {
        (mBinding.contentLayout.layoutParams as CoordinatorLayout.LayoutParams).apply {
            behavior = ContentBehavior(getContext()).apply {
                contentInitTranY = ScreenUtil.dip2pxF(getContext(), 300f)
                contentMaxTransY = ScreenUtil.dip2pxF(getContext(), 370f)
                topBarHeight = ScreenUtil.dip2pxF(getContext(), 56f) + ImmersionBar.getStatusBarHeight(getContext())
            }
        }
        (mBinding.coverLayout.layoutParams as CoordinatorLayout.LayoutParams).apply {
            behavior = CoverBehavior(getContext()).apply {
                contentInitTranY = ScreenUtil.dip2pxF(getContext(), 300f)
                contentMaxTransY = ScreenUtil.dip2pxF(getContext(), 370f)
                coverTransY = ScreenUtil.dip2pxF(getContext(), -50f)
                topBarHeight = ScreenUtil.dip2pxF(getContext(), 56f) + ImmersionBar.getStatusBarHeight(getContext())
            }
        }
        (mBinding.appBar.layoutParams as CoordinatorLayout.LayoutParams).apply {
            behavior = ToolbarBehavior(getContext()).apply {
                contentInitTranY = ScreenUtil.dip2pxF(getContext(), 300f)
                topBarHeight = ScreenUtil.dip2pxF(getContext(), 56f) + ImmersionBar.getStatusBarHeight(getContext())
            }
        }
        (mBinding.titleBar.layoutParams as CoordinatorLayout.LayoutParams).apply {
            behavior = TitleBehavior(getContext()).apply {
                contentInitTranY = ScreenUtil.dip2pxF(getContext(), 300f)
                topBarHeight = ScreenUtil.dip2pxF(getContext(), 56f) + ImmersionBar.getStatusBarHeight(getContext())
            }
        }
    }

    override fun onClick(v: View) {
        when(v) {
            mBinding.avatar -> {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, collectionDetailBean.collectionAuthorId)
                }
                startActivity(intent)
            }
            mBinding.subscribe -> {
                mPresenter?.subscribeCollection(cid, if (collectionDetailBean.isSubscribe) "unfo" else "follow")
            }
        }
    }

    override fun setOnItemClickListener() {
        sameOwnerAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this, CollectionDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.COLLECTION_ID, sameOwnerAdapter.data[position].cid)
            }
            startActivity(intent)
        }

        collectionPostAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this, NewPostDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.TOPIC_ID, collectionPostAdapter.data[position].topicId)
            }
            startActivity(intent)
        }
        collectionPostAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.avatar) {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, collectionPostAdapter.data[position].authorId)
                }
                startActivity(intent)
            }
        }
        collectionPostAdapter.setOnItemLongClickListener { adapter, view, position ->
            if (SharePrefUtil.getUid(getContext()) == collectionDetailBean.collectionAuthorId) {
                BlurAlertDialogBuilder(getContext())
                    .setMessage("确认将该帖子从专辑里移除吗？")
                    .setTitle("移除帖子")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除") { dialog, p1 ->
                        mPresenter?.deleteCollectionPost(cid, collectionPostAdapter.data[position].topicId, position)
                        dialog?.dismiss()
                    }
                    .create()
                    .show()
            }
            false
        }
    }

    override fun onOptionsSelected(item: MenuItem?) {
        super.onOptionsSelected(item)
        if (item?.itemId == R.id.menu_collection_delete) {
            BlurAlertDialogBuilder(getContext())
                .setMessage("确认删除淘专辑吗？确认后淘专辑内帖子会被清空，并删除该专辑，操作不可撤销!")
                .setTitle("删除淘专辑")
                .setPositiveButton("取消", null)
                .setNegativeButton("删除") { dialog, p1 ->
                    mPresenter?.deleteCollection(cid)
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mPresenter?.getCollectionDetail(cid, mPage)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getCollectionDetail(cid, mPage)
    }

    override fun onCoverChanged(upPercent: Float, downPercent: Float) {
        mBinding.coverContent.alpha = 1 - upPercent
        mBinding.coverImg.alpha = 1 - upPercent
        mBinding.blurImg.alpha = upPercent
    }

    override fun onGetCollectionSuccess(collectionDetailBean: CollectionDetailBean, hasNext: Boolean) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()

        if (mUnRead) {
            MessageManager.INSTANCE.decreaseCollectionCount()
            EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.SET_MSG_COUNT))
        }

        if (mPage == 1) {
            this.collectionDetailBean = collectionDetailBean
            setData(collectionDetailBean)
            collectionPostAdapter.addData(collectionDetailBean.postListBean, true)
            mBinding.recyclerView.scheduleLayoutAnimation()
        } else {
            collectionPostAdapter.addData(collectionDetailBean.postListBean, false)
        }

        if (hasNext) {
            mPage ++
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    private fun setData(collectionDetailBean: CollectionDetailBean) {
        mBinding.toolbar.menu.findItem(R.id.menu_collection_delete)?.isVisible =
            SharePrefUtil.getUid(this) == collectionDetailBean.collectionAuthorId

        mBinding.avatar.load(collectionDetailBean.collectionAuthorAvatar)
        mBinding.ownerName.text = if (SharePrefUtil.getUid(getContext()) == collectionDetailBean.collectionAuthorId) {
            "由您创建"
        } else {
            "由${collectionDetailBean.collectionAuthorName}创建"
        }
        mBinding.title.text = collectionDetailBean.collectionTitle
        mBinding.toolbar.title = collectionDetailBean.collectionTitle
        mBinding.dsp.apply {
            text = collectionDetailBean.collectionDsp
            visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        mBinding.subscribeCount.text = collectionDetailBean.subscribeCount.plus("人订阅")

        if (collectionDetailBean.collectionAuthorId == SharePrefUtil.getUid(getContext())) {
            mBinding.subscribe.visibility = View.GONE
        } else {
            mBinding.subscribe.visibility = View.VISIBLE
            mBinding.subscribe.text = if (collectionDetailBean.isSubscribe) "取消订阅" else "订阅"
        }

        if (collectionDetailBean.collectionTags.isNullOrEmpty()) {
            mBinding.tagRv.visibility = View.GONE
        } else {
            mBinding.tagRv.apply {
                visibility = View.VISIBLE
                adapter = CollectionTagAdapter(R.layout.item_collection_tag).apply {
                    setNewData(collectionDetailBean.collectionTags)
                }
            }
        }
        if (collectionDetailBean.mSameOwnerCollection.isNullOrEmpty()) {
            mBinding.sameOwnerCollectionText.visibility = View.GONE
            mBinding.sameOwnerCollectionRv.visibility = View.GONE
        } else {
            mBinding.sameOwnerCollectionText.visibility = View.VISIBLE
            mBinding.sameOwnerCollectionRv.apply {
                visibility = View.VISIBLE
                adapter = sameOwnerAdapter
            }
            sameOwnerAdapter.setNewData(collectionDetailBean.mSameOwnerCollection)
        }

        if (collectionDetailBean.mRecentSubscriberBean.isNullOrEmpty()) {
            mBinding.subscribeAvatar1.visibility = View.GONE
            mBinding.subscribeAvatar2.visibility = View.GONE
            mBinding.subscribeAvatar3.visibility = View.GONE
            mBinding.subscribeAvatarMore.visibility = View.GONE
        } else {
            for ((index, item) in collectionDetailBean.mRecentSubscriberBean.withIndex()) {
                if (index == 0) {
                    mBinding.subscribeAvatar3.apply {
                        visibility = View.VISIBLE
                        load(item.userAvatar)
                    }
                }
                if (index == 1) {
                    mBinding.subscribeAvatar2.apply {
                        visibility = View.VISIBLE
                        load(item.userAvatar)
                    }
                }
                if (index == 2) {
                    mBinding.subscribeAvatar1.apply {
                        visibility = View.VISIBLE
                        load(item.userAvatar)
                    }
                }
                if (index == 3) {
                    mBinding.subscribeAvatarMore.apply {
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        Glide
            .with(this)
            .asDrawable()
            .load(collectionDetailBean.collectionAuthorAvatar)
            .into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                    try {
                        val bitmap = if (resource is GifDrawable) resource.firstFrame else ImageUtil.drawable2Bitmap(resource)
                        if (bitmap != null) {
                            mBinding.coverImg.setImageBitmap(ImageUtil.blur(this@CollectionDetailActivity, bitmap, 25f))
                            setBlurBg(bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
    }

    private fun setBlurBg(bitmap: Bitmap) {
        val colors = intArrayOf(
            Color.parseColor("#4D000000"),
            ColorUtil.getAlphaColor(0.6f, Color.parseColor("#4D000000"))
        )

        val palette = Palette.from(bitmap).generate()
        val vibrantSwatch = palette.vibrantSwatch
        val mutedSwatch = palette.mutedSwatch

        if (mutedSwatch != null) {
            colors[0] = mutedSwatch.rgb
            colors[1] = ColorUtil.getAlphaColor(0.6f, mutedSwatch.rgb)
        } else if (vibrantSwatch != null) {
            colors[0] = vibrantSwatch.rgb
            colors[1] = ColorUtil.getAlphaColor(0.6f, vibrantSwatch.rgb)
        }

        mBinding.blurImg.background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
    }

    override fun onGetCollectionError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (mPage == 1) {
            if (collectionPostAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.statusView.error(msg)
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onSubscribeCollectionSuccess(subscribe: Boolean) {
        collectionDetailBean.isSubscribe = subscribe
        mBinding.subscribe.text = if (collectionDetailBean.isSubscribe) "取消订阅" else "订阅"
        showToast(if (subscribe) "订阅成功" else "取消订阅成功", ToastType.TYPE_SUCCESS)
    }

    override fun onSubscribeCollectionError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onDeleteCollectionPostSuccess(msg: String?, position: Int) {
        showToast(msg, ToastType.TYPE_SUCCESS)
        collectionPostAdapter.data.removeAt(position)
        collectionPostAdapter.notifyItemRemoved(position)
    }

    override fun onDeleteCollectionPostError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onDeleteCollectionSuccess(msg: String?) {
        showToast(msg, ToastType.TYPE_SUCCESS)
        finish()
    }

    override fun onDeleteCollectionError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun getContext() = this

}