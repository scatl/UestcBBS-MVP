package com.scatl.uestcbbs.module.post.view

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseEvent.AddPoll
import com.scatl.uestcbbs.base.BaseVBFragment
import com.scatl.uestcbbs.base.BaseVBFragmentForBottom
import com.scatl.uestcbbs.databinding.FragmentAddVoteBinding
import com.scatl.uestcbbs.helper.VerticalDragHelper
import com.scatl.uestcbbs.module.post.adapter.AddVoteAdapter
import com.scatl.uestcbbs.module.post.presenter.AddVotePresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.NumberUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.dialog.BlurAlertDialogBuilder
import org.greenrobot.eventbus.EventBus
import java.util.Collections

/**
 * Created by sca_tl at 2023/5/24 16:13
 */
class AddVoteFragment: BaseVBFragment<AddVotePresenter, AddVoteView, FragmentAddVoteBinding>(), AddVoteView, VerticalDragHelper.DragCallBack {

    private val DEFAULT_OPTIONS = arrayListOf("", "")

    private lateinit var voteAdapter: AddVoteAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private var mOptions = DEFAULT_OPTIONS
    private var mExpirationDays = 3
    private var mMaxChoices = 1
    private var mVisibleAfterVoted = false
    private var mShowVoters = false

    private var mHasEmptyContent = false
    private var mEmptyContentIndex = 0

    override fun getViewBinding() = FragmentAddVoteBinding.inflate(layoutInflater)

    override fun initPresenter() = AddVotePresenter()

    companion object {
        fun getInstance(bundle: Bundle?) = AddVoteFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        bundle?.let {
            mOptions = it.getStringArrayList(Constant.IntentKey.POLL_OPTIONS) ?: DEFAULT_OPTIONS
            mExpirationDays = it.getInt(Constant.IntentKey.POLL_EXPIRATION, 3)
            mMaxChoices = it.getInt(Constant.IntentKey.POLL_CHOICES, 1)
            mVisibleAfterVoted = it.getBoolean(Constant.IntentKey.POLL_VISIBLE, false)
            mShowVoters = it.getBoolean(Constant.IntentKey.POLL_SHOW_VOTERS, false)

            if (mOptions.size < 2) {
                mOptions = DEFAULT_OPTIONS
            }
            if (mExpirationDays <= 0) {
                mExpirationDays = 3
            }
            if (mMaxChoices <= 0 || mMaxChoices > mOptions.size) {
                mMaxChoices = 1
            }
        }
    }

    override fun initView() {
        mBinding.expirationDaysAddBtn.setOnClickListener(this)
        mBinding.expirationDaysRemoveBtn.setOnClickListener(this)
        mBinding.choicesCountAddBtn.setOnClickListener(this)
        mBinding.choicesCountRemoveBtn.setOnClickListener(this)
        mBinding.confirmBtn.setOnClickListener(this)
        mBinding.deleteBtn.setOnClickListener(this)
        mBinding.addOptionBtn.setOnClickListener(this)
        mBinding.visibleAfterVoteBtn.isChecked = mVisibleAfterVoted
        mBinding.showVotersBtn.isChecked = mShowVoters

        voteAdapter = AddVoteAdapter(R.layout.item_add_vote)
        mBinding.optionsRv.apply {
            adapter = voteAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 8f)
                    outRect.left = ScreenUtil.dip2px(context, 16f)
                    outRect.right = ScreenUtil.dip2px(context, 16f)
                }
            })
        }
        val dragHelper = VerticalDragHelper(false)
        dragHelper.dragCallBack = this
        mItemTouchHelper = ItemTouchHelper(dragHelper)
        mItemTouchHelper.attachToRecyclerView(mBinding.optionsRv)

        mBinding.expirationDays.text = mExpirationDays.toString()
        mBinding.choicesCount.text = mMaxChoices.toString()

        voteAdapter.setNewData(mOptions)
    }

    override fun onClick(v: View) {
        when(v) {
            mBinding.expirationDaysAddBtn -> {
                if (mExpirationDays >= 3) {
                    mExpirationDays = 3
                    mBinding.expirationDays.text = "3"
                } else {
                    mExpirationDays ++
                    mBinding.expirationDays.text = mExpirationDays.toString()
                }
            }
            mBinding.expirationDaysRemoveBtn -> {
                if (mExpirationDays <= 1) {
                    mExpirationDays = 1
                    mBinding.expirationDays.text = "1"
                } else {
                    mExpirationDays --
                    mBinding.expirationDays.text = mExpirationDays.toString()
                }
            }
            mBinding.choicesCountAddBtn -> {
                if (mMaxChoices >= voteAdapter.data.size) {
                    mMaxChoices = voteAdapter.data.size
                    mBinding.choicesCount.text = voteAdapter.data.size.toString()
                } else {
                    mMaxChoices ++
                    mBinding.choicesCount.text = mMaxChoices.toString()
                }
            }
            mBinding.choicesCountRemoveBtn -> {
                if (mMaxChoices <= 1) {
                    mMaxChoices = 1
                    mBinding.choicesCount.text = "1"
                } else {
                    mMaxChoices --
                    mBinding.choicesCount.text = mMaxChoices.toString()
                }
            }
            mBinding.confirmBtn -> {
                onConfirm()
            }
            mBinding.deleteBtn -> {
                BlurAlertDialogBuilder(requireContext())
                    .setMessage("确认删除投票？")
                    .setTitle("删除投票")
                    .setPositiveButton("取消", null)
                    .setNegativeButton("确认") { dialog, p1 ->
                        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.DELETE_POLL))
                        dialog.dismiss()
                        (parentFragment as? BaseVBFragmentForBottom)?.dismiss()
                    }
                    .create()
                    .show()
            }
            mBinding.addOptionBtn -> {
                voteAdapter.addData("")
                voteAdapter.notifyItemInserted(voteAdapter.data.size)
                (0 .. 1).forEach {
                    voteAdapter.refreshNotifyItemChanged(it, AddVoteAdapter.PAYLOAD_DELETE)
                }
                (mBinding.optionsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(voteAdapter.data.size - 1, 0)
            }
        }
    }

    override fun setOnItemClickListener() {
        voteAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.icon_delete) {
                if (mMaxChoices == voteAdapter.data.size) {
                    mMaxChoices = voteAdapter.data.size - 1
                    mBinding.choicesCount.text = mMaxChoices.toString()
                }
                voteAdapter.data.removeAt(position)
                voteAdapter.notifyItemRemoved(position)
                (0 .. position).forEach {
                    voteAdapter.refreshNotifyItemChanged(it, AddVoteAdapter.PAYLOAD_DELETE)
                }
                voteAdapter.notifyItemRangeChanged(position, voteAdapter.data.size - position)
            }
            if (view.id == R.id.icon_drag) {
                showToast("长按我可以拖拽调整顺序", ToastType.TYPE_NORMAL)
            }
        }
        voteAdapter.setOnItemChildLongClickListener { adapter, view, position ->
            if (view.id == R.id.icon_drag) {
                mBinding.optionsRv.findViewHolderForAdapterPosition(position)?.let {
                    mItemTouchHelper.startDrag(it)
                }
            }
            true
        }
    }

    private fun onConfirm() {
        if (hasEmptyContent()) {
            showToast("第" + (mEmptyContentIndex + 1) + "个选项的描述不能为空", ToastType.TYPE_ERROR)
        } else {
            val addPoll = AddPoll()
            addPoll.pollOptions = voteAdapter.data
            addPoll.pollChoice = NumberUtil.parseInt(mBinding.choicesCount.text.toString(), 1)
            addPoll.pollExp = NumberUtil.parseInt(mBinding.expirationDays.text.toString(), 3)
            addPoll.pollVisible = mBinding.visibleAfterVoteBtn.isChecked
            addPoll.showVoters = mBinding.showVotersBtn.isChecked
            EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.ADD_POLL, addPoll))
            (parentFragment as? BaseVBFragmentForBottom)?.dismiss()
        }
    }

    private fun hasEmptyContent(): Boolean {
        mHasEmptyContent = false
        for ((i, data) in voteAdapter.data.withIndex()) {
            if (data.replace("\n", "").replace(" ", "").isEmpty()) {
                mEmptyContentIndex = i
                mHasEmptyContent = true
                break
            }
        }
        return mHasEmptyContent
    }

    override fun onItemMoved(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        Collections.swap(voteAdapter.data, fromPosition, toPosition)
        voteAdapter.notifyItemMoved(fromPosition, toPosition)
        voteAdapter.refreshNotifyItemChanged(fromPosition, AddVoteAdapter.PAYLOAD_EXCHANGE)
        voteAdapter.refreshNotifyItemChanged(toPosition, AddVoteAdapter.PAYLOAD_EXCHANGE)
    }
}