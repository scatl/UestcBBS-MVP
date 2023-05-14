package com.scatl.uestcbbs.module.credit.view

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseFragment
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.databinding.FragmentWaterTaskDoingBinding
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.module.credit.adapter.WaterTaskAdapter
import com.scatl.uestcbbs.module.credit.presenter.WaterTaskDonePresenter
import com.scatl.uestcbbs.module.credit.presenter.WaterTaskFailedPresenter
import com.scatl.uestcbbs.util.showToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus

/**
 * created by sca_tl at 2023/4/7 20:32
 */
class WaterTaskFailedFragment: BaseVBFragment<WaterTaskFailedPresenter, WaterTaskFailView, FragmentWaterTaskDoingBinding>(), WaterTaskFailView {

    private lateinit var mAdapter: WaterTaskAdapter

    companion object {
        fun getInstance(bundle: Bundle?) = WaterTaskFailedFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {

    }

    override fun getViewBinding() = FragmentWaterTaskDoingBinding.inflate(layoutInflater)

    override fun initPresenter() = WaterTaskFailedPresenter()

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
        mPresenter?.getDoneTaskList()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getDoneTaskList()
    }

    override fun setOnItemClickListener() {
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.apply_task -> {
                    mPresenter?.applyNewTask(mAdapter.data[position].id, position)
                }
            }
        }
    }

    override fun onGetFailedTaskSuccess(taskBeans: List<TaskBean>, formhash: String?) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()

        if (taskBeans.isEmpty()) {
            mBinding.statusView.error("啊哦，还没有失败的任务~")
        } else {
            mAdapter.setNewData(taskBeans)
            mBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun onGetFailedTaskError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.statusView.error(msg)
    }

    override fun onApplyNewTaskSuccess(msg: String?, taskId: Int, position: Int) {
        mAdapter.data.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        showToast(msg, ToastType.TYPE_SUCCESS)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.APPLY_NEW_TASK_SUCCESS))
    }

    override fun onApplyNewTaskError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }
}