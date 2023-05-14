package com.scatl.uestcbbs.module.credit.view

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentWaterTaskDoingBinding
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.module.credit.adapter.WaterTaskAdapter
import com.scatl.uestcbbs.module.credit.presenter.WaterTaskDoingPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.showToast
import com.scatl.widget.dialog.BlurAlertDialogBuilder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/4/6 20:43
 */
class WaterTaskDoingFragment: BaseVBFragment<WaterTaskDoingPresenter, WaterTaskDoingView, FragmentWaterTaskDoingBinding>(), WaterTaskDoingView {

    private var mTaskType = TaskType.TYPE_DOING
    private lateinit var mAdapter: WaterTaskAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskDoingFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        mTaskType = bundle?.getString(Constant.IntentKey.TYPE, TaskType.TYPE_DOING)?:TaskType.TYPE_DOING
    }

    override fun getViewBinding() = FragmentWaterTaskDoingBinding.inflate(layoutInflater)

    override fun initPresenter() = WaterTaskDoingPresenter()

    override fun initView() {
        super.initView()
        mAdapter = WaterTaskAdapter(R.layout.item_water_task_doing)
        mBinding.recyclerView.apply {
            adapter = mAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_top)
        }

        mBinding.statusView.loading()
    }

    override fun lazyLoad() {
        mPresenter?.getDoingTaskList()
    }

    override fun setOnItemClickListener() {
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.apply_award -> {
                    mPresenter?.getTaskAward(position, mAdapter.data[position].id)
                }
                R.id.time_left -> {
                    if (mAdapter.data[position].progress < 100) {
                        mPresenter?.checkLeftTime(mAdapter.data[position].id, view as TextView)
                    }
                }
                R.id.delete -> {
                    BlurAlertDialogBuilder(requireContext())
                        .setPositiveButton("取消", null)
                        .setNegativeButton("确认") { dialog, which ->
                            mPresenter?.deleteDoingTask(mAdapter.data[position].id, position)
                        }
                        .setMessage("放弃该任务后，进度会重置。确认放弃吗？")
                        .setTitle("放弃任务")
                        .create()
                        .show()
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getDoingTaskList()
    }

    override fun onGetDoingTaskSuccess(taskBeans: List<TaskBean>, formhash: String?) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (taskBeans.isEmpty()) {
            mBinding.statusView.error("啊哦，没有进行中的任务，快去领取新任务吧~")
        } else {
            mAdapter.setNewData(taskBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetDoingTaskError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.statusView.error(msg)
    }

    override fun onDeleteDoingTaskSuccess(msg: String?, position: Int) {
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.DELETE_TASK_SUCCESS))
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }

    override fun onDeleteDoingTaskError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onGetTaskAwardSuccess(msg: String?, position: Int) {
        showToast(msg, ToastType.TYPE_SUCCESS)
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }

    override fun onGetTaskAwardError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onCheckLeftTimeSuccess(leftTime: String?, textView: TextView?) {
        textView?.text = "剩余时间：".plus(leftTime)
        showToast("加油", ToastType.TYPE_NORMAL)
    }

    override fun onCheckLeftTimeError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun registerEventBus() = true

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.APPLY_NEW_TASK_SUCCESS) {
            mPresenter?.getDoingTaskList()
        }
    }
}