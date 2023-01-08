package com.scatl.uestcbbs.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by sca_tl on 2023/1/6 9:38
 */
abstract class BaseVBFragment<P: BaseVBPresenter<V>, V: BaseView, VB: ViewBinding> : Fragment(), View.OnClickListener,
    OnRefreshListener, OnLoadMoreListener {

    protected lateinit var mBinding: VB
    protected var mPresenter: P? = null
    private var isLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundle(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = getViewBinding()
        mPresenter = initPresenter()
        mPresenter?.attachView(this as V)

        initView()

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoad) {
            isLoad = true
            lazyLoad()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        mPresenter?.detachView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventBusReceived(baseEvent: BaseEvent<Any>?) {
        baseEvent?.let {
            receiveEventBusMsg(it)
        }
    }

    override fun onClick(v: View) { }
    override fun onRefresh(refreshLayout: RefreshLayout) { }
    override fun onLoadMore(refreshLayout: RefreshLayout) { }
    protected abstract fun getViewBinding(): VB
    protected open fun getBundle(bundle: Bundle?) { }
    protected abstract fun initView()
    protected abstract fun initPresenter(): P
    protected open fun lazyLoad() { }
    protected open fun registerEventBus() = false
    protected open fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) { }
}