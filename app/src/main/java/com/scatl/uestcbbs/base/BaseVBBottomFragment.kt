package com.scatl.uestcbbs.base

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.scatl.uestcbbs.R
import com.scatl.viewpager_bottomsheet.ViewPagerBottomSheetBehavior
import com.scatl.viewpager_bottomsheet.ViewPagerBottomSheetDialog
import com.scatl.viewpager_bottomsheet.ViewPagerBottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by sca_tl on 2022/12/30 14:19
 */
abstract class BaseVBBottomFragment<P: BaseVBPresenter<V>, V: BaseView, VB: ViewBinding> :
                                ViewPagerBottomSheetDialogFragment(), View.OnClickListener {

    protected lateinit var mBehavior: ViewPagerBottomSheetBehavior<View>
    protected lateinit var binding: VB
    protected var presenter: P? = null

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = getViewBinding()

        val bottomSheetDialog = (super.onCreateDialog(savedInstanceState) as ViewPagerBottomSheetDialog).apply {
            setContentView(binding.root)
            delegate
                .findViewById<View>(R.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.shape_dialog_fragment)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bottomSheetDialog.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                attributes.blurBehindRadius = 64
            }
        }

        mBehavior = ViewPagerBottomSheetBehavior.from(binding.root.parent as View)

        val maxHeightMultiplier = if (setMaxHeightMultiplier() <= 0) 0.5 else setMaxHeightMultiplier()
        if (!isDraggable()) {
            mBehavior.isDraggable = false
            mBehavior.peekHeight = (resources.displayMetrics.heightPixels * maxHeightMultiplier).toInt()
        }

        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (resources.displayMetrics.heightPixels * maxHeightMultiplier).toInt()
        }

        getBundle(arguments)

        presenter = initPresenter()
        presenter?.attachView(this as V)

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
        presenter?.detachView()
    }

    override fun onClick(v: View) {

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