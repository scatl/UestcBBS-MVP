package com.scatl.uestcbbs.base

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.scatl.uestcbbs.R
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetDialog
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetDialogFragment
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by sca_tl on 2022/12/30 14:19
 */
abstract class BaseVBBottomFragment<P: BaseVBPresenter<V>, V: BaseView, VB: ViewBinding> :
    ViewPagerBottomSheetDialogFragment(), View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    protected lateinit var mBehavior: ViewPagerBottomSheetBehavior<View>
    protected lateinit var mBinding: VB
    protected var mPresenter: P? = null

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = getViewBinding()

        val bottomSheetDialog = (super.onCreateDialog(savedInstanceState) as ViewPagerBottomSheetDialog).apply {
            setContentView(mBinding.root)
            delegate
                .findViewById<View>(R.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.csu_shape_activity_round_corner)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bottomSheetDialog.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                attributes.blurBehindRadius = 64
            }
        }

        getBundle(arguments)

        mBehavior = ViewPagerBottomSheetBehavior.from(mBinding.root.parent as View)

        val maxHeightMultiplier = if (setMaxHeightMultiplier() <= 0) 0.5 else setMaxHeightMultiplier()
        if (!isDraggable()) {
            mBehavior.isDraggable = false
            mBehavior.peekHeight = (resources.displayMetrics.heightPixels * maxHeightMultiplier).toInt()
        }

        mBinding.root.layoutParams = mBinding.root.layoutParams.apply {
            height = (resources.displayMetrics.heightPixels * maxHeightMultiplier).toInt()
        }

        mPresenter = initPresenter()
        mPresenter?.attachView(this as V)

        initView()

        return bottomSheetDialog
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEventBusReceived(baseEvent: BaseEvent<Any>?) {
        baseEvent?.let {
            receiveEventBusMsg(it)
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.detachView()
    }

    override fun onClick(v: View) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {

    }

    protected abstract fun getViewBinding(): VB
    protected abstract fun initView()
    protected abstract fun initPresenter(): P
    protected open fun setMaxHeightMultiplier() = 0.8
    protected open fun getBundle(bundle: Bundle?) { }
    protected open fun registerEventBus() = false
    protected open fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) { }
    protected open fun isDraggable() = true

}