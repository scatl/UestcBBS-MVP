package com.scatl.uestcbbs.base

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.jaeger.library.StatusBarUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.util.ScreenUtil
import com.scatl.util.TheftProofMark
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by sca_tl on 2023/1/3 16:00
 */
abstract class BaseVBActivity<P: BaseVBPresenter<V>, V: BaseView, VB: ViewBinding> :
    BaseGrayActivity(), View.OnClickListener, View.OnLongClickListener, OnRefreshListener, OnLoadMoreListener {

    protected lateinit var mBinding: VB
    protected var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBar()
        super.onCreate(savedInstanceState)

        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (mode == Configuration.UI_MODE_NIGHT_YES) {
            StatusBarUtil.setDarkMode(this)
        } else {
            StatusBarUtil.setLightMode(this)
        }

        mBinding = getViewBinding()
        setContentView(mBinding.root)
        getIntent(intent)
        mPresenter = initPresenter()
        mPresenter?.attachView(this as V)
        initView(false)
        setOnItemClickListener()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventBusReceived(baseEvent: BaseEvent<Any>?) {
        baseEvent?.let { receiveEventBusMsg(it) }
    }

    override fun onStart() {
        super.onStart()
        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        mPresenter?.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //确保子fragment调用onActivityResult方法
        supportFragmentManager.fragments
        if (supportFragmentManager.fragments.size > 0) {
            val fragments = supportFragmentManager.fragments
            for (mFragment in fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onClick(v: View) { }
    override fun onLongClick(v: View) = true

    override fun onRefresh(refreshLayout: RefreshLayout) { }
    override fun onLoadMore(refreshLayout: RefreshLayout) { }

    protected open fun setStatusBar() {
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"), 0)
    }
    protected abstract fun getViewBinding(): VB
    protected open fun getIntent(intent: Intent?) {}
    protected open fun initView(theftProof: Boolean) {
        if (theftProof) {
            try {
                TheftProofMark
                    .getInstance()
                    .setTextSize(ScreenUtil.sp2px(this, 50f))
                    .setTextColor(getColor(R.color.theft_proof_color))
                    .show(this, SharePrefUtil.getUid(this).toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        (findViewById<View>(R.id.toolbar) as? Toolbar)?.apply {
            setNavigationOnClickListener { v: View? -> finishAfterTransition() }
            setOnMenuItemClickListener { item: MenuItem? ->
                onOptionsSelected(item)
                true
            }
        }
        (findViewById<View>(R.id.refresh_layout) as? SmartRefreshLayout)?.apply {
            setOnRefreshListener(this@BaseVBActivity)
            setOnLoadMoreListener(this@BaseVBActivity)
        }
    }
    protected abstract fun initPresenter(): P
    protected open fun setOnItemClickListener() {}
    protected open fun onOptionsSelected(item: MenuItem?) {}
    protected open fun registerEventBus() = false
    protected open fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) { }

    fun bindClickEvent(vararg views: View) {
        views.forEach { it.setOnClickListener(this) }
    }
}