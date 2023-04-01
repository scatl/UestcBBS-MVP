package com.scatl.uestcbbs.module.board.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.HapticFeedbackConstants
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.shape.ShapeAppearanceModel
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.databinding.FragmentSelectBoardBinding
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.entity.SelectBoardResultEvent
import com.scatl.uestcbbs.entity.SingleBoardBean
import com.scatl.uestcbbs.entity.SingleBoardBean.ClassificationTypeListBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.module.board.presenter.SelectBoardPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.common.ScreenUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by sca_tl at 2023/3/31 10:50
 */
class SelectBoardFragment: BaseVBBottomFragment<SelectBoardPresenter, SelectBoardView, FragmentSelectBoardBinding>(), SelectBoardView {

    private var mNeedConfirm = false
    private var mSelectBoardResultEvent = SelectBoardResultEvent()

    companion object {
        fun getInstance(bundle: Bundle?) = SelectBoardFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        mNeedConfirm = bundle?.getBoolean(Constant.IntentKey.NEED_CONFIRM, false)?: false
    }

    override fun getViewBinding() = FragmentSelectBoardBinding.inflate(layoutInflater)

    override fun initPresenter() = SelectBoardPresenter()

    override fun initView() {
        mBinding.confirmBtn.setOnClickListener(this)
        mBinding.statusView.loading(mBinding.scrollView)
        mPresenter?.getMainForumList()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (v == mBinding.confirmBtn) {
            dismiss()
        }
    }

    override fun onGetMainBoardListSuccess(forumListBean: ForumListBean) {
        mBinding.statusView.success()
        forumListBean.list?.forEach { main ->
            val chip = getChip(main.board_category_name)
            chip.tag = main.board_list
            chip.setTag(R.id.data, main)
            chip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mBinding.fatherBoardsGroup.removeAllViews()
                mBinding.childBoardTitle.visibility = View.GONE
                mBinding.childBoardsGroup.visibility = View.GONE
                mBinding.classificationBoardTitle.visibility = View.GONE
                mBinding.classificationBoardsGroup.visibility = View.GONE
                mBinding.confirmBtn.visibility = View.GONE

                mSelectBoardResultEvent.boardCategoryId = (v.getTag(R.id.data) as? ForumListBean.ListBean)?.board_category_id
                mSelectBoardResultEvent.boardCategoryName = (v.getTag(R.id.data) as? ForumListBean.ListBean)?.board_category_name

                (chip.tag as? List<ForumListBean.ListBean.BoardListBean>?)?.forEach { father ->
                    val fatherChip = getChip(father.board_name)
                    fatherChip.setTag(R.id.data, father)
                    fatherChip.setOnClickListener { v ->
                        v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        mBinding.classificationBoardTitle.visibility = View.GONE
                        mBinding.classificationBoardsGroup.visibility = View.GONE
                        mBinding.childBoardTitle.visibility = View.GONE
                        mBinding.childBoardsGroup.visibility = View.GONE
                        mBinding.confirmBtn.visibility = View.GONE

                        mSelectBoardResultEvent.fatherBoardId = (v.getTag(R.id.data) as? ForumListBean.ListBean.BoardListBean)?.board_id
                        mSelectBoardResultEvent.fatherBName = (v.getTag(R.id.data) as? ForumListBean.ListBean.BoardListBean)?.board_name

                        mPresenter?.getChildBoardList(mSelectBoardResultEvent.fatherBoardId!!, mSelectBoardResultEvent.fatherBName!!)
                    }
                    mBinding.fatherBoardTitle.visibility = View.VISIBLE
                    mBinding.fatherBoardsGroup.addView(fatherChip)
                }
            }

            mBinding.mainBoardsGroup.addView(chip)
        }
    }

    override fun onGetMainBoardListError(msg: String?) {
        mBinding.statusView.error()
        showToast("加载版块失败", ToastType.TYPE_ERROR)
    }

    override fun onGetChildBoardListSuccess(subForumListBean: SubForumListBean) {
        mBinding.childBoardsGroup.removeAllViews()
        mBinding.childBoardTitle.visibility = View.VISIBLE
        mBinding.childBoardsGroup.visibility = View.VISIBLE
        subForumListBean.list?.getOrNull(0)?.board_list?.forEach { child ->
            val childChip = getChip(child.board_name)
            childChip.setTag(R.id.data, child)
            childChip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mBinding.confirmBtn.visibility = View.GONE
                mBinding.classificationBoardTitle.visibility = View.GONE
                mBinding.classificationBoardsGroup.visibility = View.GONE
                mSelectBoardResultEvent.childBoardId = (v.getTag(R.id.data) as? SubForumListBean.ListBean.BoardListBean)?.board_id
                mSelectBoardResultEvent.childBoardName= (v.getTag(R.id.data) as? SubForumListBean.ListBean.BoardListBean)?.board_name
                mSelectBoardResultEvent.childBoardId?.let { mPresenter?.getClassification(it) }
            }
            mBinding.childBoardsGroup.addView(childChip)
        }
    }

    override fun onGetChildBoardListError(msg: String?) {
        showToast("加载版块失败", ToastType.TYPE_ERROR)
    }

    override fun onGetClassificationSuccess(singleBoardBean: SingleBoardBean) {
        mBinding.classificationBoardsGroup.removeAllViews()
        mBinding.classificationBoardTitle.visibility = View.VISIBLE
        mBinding.classificationBoardsGroup.visibility = View.VISIBLE
        singleBoardBean.classificationType_list?.forEach { classification ->
            val classificationChip = getChip(classification.classificationType_name)
            classificationChip.setTag(R.id.data, classification)
            classificationChip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mSelectBoardResultEvent.classificationId = (v.getTag(R.id.data) as? ClassificationTypeListBean)?.classificationType_id
                mSelectBoardResultEvent.classificationName= (v.getTag(R.id.data) as? ClassificationTypeListBean)?.classificationType_name
                if (mNeedConfirm) {
                    mBinding.confirmBtn.visibility = View.VISIBLE
                } else {
                    dismiss()
                }
            }
            mBinding.classificationBoardsGroup.addView(classificationChip)
        }
    }

    override fun onGetClassificationError(msg: String?) {
        showToast("加载分类失败", ToastType.TYPE_ERROR)
    }

    override fun setMaxHeightMultiplier() = 0.9

    override fun dismiss() {
        EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.BOARD_SELECTED, mSelectBoardResultEvent))
        super.dismiss()
    }

    private fun getChip(txt: String) = Chip(ContextThemeWrapper(context, R.style.Widget_Material3_Chip_Filter)).apply {
        text = txt
        isCheckable = true
        chipStrokeWidth = 0f
        chipIcon = null
        checkedIcon = null
        setChipBackgroundColorResource(R.color.select_board_chip_bg_color)
        shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            setAllCornerSizes(ScreenUtil.dip2pxF(requireContext(), 20f))
        }.build()
        chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#00000000"))
    }

}