package com.scatl.uestcbbs.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.jaeger.library.StatusBarUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.util.ColorUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.DownloadUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.uestcbbs.widget.GrayFrameLayout
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by sca_tl on 2023/1/3 16:00
 */
abstract class BaseVBActivity<P: BaseVBPresenter<V>, V: BaseView, VB: ViewBinding> :
    AppCompatActivity(), View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

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
        initView()
        setOnItemClickListener()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if ("FrameLayout" == name) {
            for (i in 0 until attrs.attributeCount) {
                if (attrs.getAttributeName(i) == "id") {
                    val id = attrs.getAttributeValue(i)?.substring(1)?.toInt() ?: -1
                    val idVal = resources.getResourceName(id)
                    if ("android:id/content" == idVal) {
                        return GrayFrameLayout(context, attrs)
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs)
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
        if (requestCode == Constant.RequestCode.REQUEST_DOWNLOAD_PERMISSION && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    val uriTree = data.data
                    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uriTree!!, takeFlags)
                    SharePrefUtil.setDownloadFolderUri(this, uriTree.toString())
                    DownloadUtil.prepareDownload(this, SharePrefUtil.getDownloadFileName(this), SharePrefUtil.getDownloadFileUrl(this))
                } catch (e: Exception) {
                    showToast("授权失败：" + e.message, ToastType.TYPE_ERROR)
                }
            }
        }
    }

    override fun onClick(v: View) { }
    override fun onRefresh(refreshLayout: RefreshLayout) { }
    override fun onLoadMore(refreshLayout: RefreshLayout) { }

    protected open fun setStatusBar() {
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"), 0)
    }
    protected abstract fun getViewBinding(): VB
    protected open fun getIntent(intent: Intent?) {}
    protected open fun initView() {
        (findViewById<View>(R.id.toolbar) as? Toolbar)?.apply {
            setNavigationOnClickListener { v: View? -> finish() }
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
}