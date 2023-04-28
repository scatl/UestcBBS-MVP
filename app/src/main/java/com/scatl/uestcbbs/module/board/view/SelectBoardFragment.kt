package com.scatl.uestcbbs.module.board.view

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.google.android.material.shape.ShapeAppearanceModel
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBBottomFragment
import com.scatl.uestcbbs.databinding.FragmentSelectBoardBinding
import com.scatl.uestcbbs.entity.CommonPostBean
import com.scatl.uestcbbs.entity.ForumListBean
import com.scatl.uestcbbs.entity.SelectBoardFavoriteBean
import com.scatl.uestcbbs.entity.SelectBoardResultEvent
import com.scatl.uestcbbs.entity.SingleBoardBean
import com.scatl.uestcbbs.entity.SubForumListBean
import com.scatl.uestcbbs.module.board.presenter.SelectBoardPresenter
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.bottomsheet.ViewPagerBottomSheetBehavior
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal

/**
 * Created by sca_tl at 2023/3/31 10:50
 */
class SelectBoardFragment: BaseVBBottomFragment<SelectBoardPresenter, SelectBoardView, FragmentSelectBoardBinding>(), SelectBoardView {

    private var mNeedConfirm = false
    private var mSelectBoardResultEvent = SelectBoardResultEvent()
    private var mStatus = Status.SELECT_BOARD
    private var mFavoriteGroupH = 0

    companion object {
        fun getInstance(bundle: Bundle?) = SelectBoardFragment().apply { arguments = bundle }
    }

    override fun getBundle(bundle: Bundle?) {
        mNeedConfirm = bundle?.getBoolean(Constant.IntentKey.NEED_CONFIRM, false)?: false
    }

    override fun getViewBinding() = FragmentSelectBoardBinding.inflate(layoutInflater)

    override fun initPresenter() = SelectBoardPresenter()

    override fun initView() {
        Handler(Looper.getMainLooper()).post {
            mBehavior.state = ViewPagerBottomSheetBehavior.STATE_EXPANDED
        }
        mBinding.expandFavorite.setOnClickListener(this)
        mBinding.confirmBtn.setOnClickListener(this)
        mBinding.statusView.loading(mBinding.scrollView)
        initFavorite()
        mPresenter?.getMainForumList()
    }

    private fun initFavorite() {
        mBinding.favoriteGroup.removeAllViews()
        val favorites = LitePal
            .where("uid = ?", SharePrefUtil.getUid(context).toString())
            .find(SelectBoardFavoriteBean::class.java)
        for (item in favorites) {
            val chip = getChip(item.boardName!!.plus("-").plus(item.classificationName))
            chip.setOnClickListener {
                mSelectBoardResultEvent.childBoardId = item?.boardId
                mSelectBoardResultEvent.childBoardName = item?.boardName
                mSelectBoardResultEvent.classificationId = item?.classificationId
                mSelectBoardResultEvent.classificationName = item?.classificationName
                dismiss()
            }
            chip.setOnLongClickListener {
                mBinding.favoriteGroup.removeView(it)
                mBinding.favoriteGroup.requestLayout()
                LitePal.deleteAll(
                    SelectBoardFavoriteBean::class.java,
                    "boardId = ? and classificationId = ? and uid = ?",
                    item.boardId.toString(),
                    item.classificationId.toString(),
                    SharePrefUtil.getUid(context).toString()
                )
                true
            }
            mBinding.favoriteGroup.addView(chip)
        }

        val addFavoriteChip = getAddFavoriteChip()
        addFavoriteChip.setOnClickListener {
            reset()
            addFavoriteChip.isChecked = true
            mStatus = Status.SELECT_FAVORITE
            addFavoriteChip.text = "添加中..."
            showToast("请依次点击板块添加至常用", ToastType.TYPE_NORMAL)
        }
        mBinding.favoriteGroup.addView(addFavoriteChip)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (v == mBinding.confirmBtn) {
            if (mStatus == Status.SELECT_FAVORITE) {
                addFavoriteToDB()
                initFavorite()
                reset()
            } else {
                dismiss()
            }
        } else if (v == mBinding.expandFavorite) {
            if (mBinding.favoriteGroup.visibility == View.GONE) {
                mBinding.favoriteGroup.visibility = View.VISIBLE
                ValueAnimator
                    .ofInt(1, mFavoriteGroupH)
                    .setDuration(400)
                    .apply {
                        addUpdateListener {
                            mBinding.favoriteGroup.layoutParams.apply {
                                height = it.animatedValue as Int
                            }
                            mBinding.favoriteGroup.requestLayout()
                            mBinding.favoriteDeleteTip.alpha = it.animatedValue as Int / mFavoriteGroupH.toFloat()
                            mBinding.expandFavorite.rotation = 180 - (it.animatedValue as Int / mFavoriteGroupH.toFloat()) * 180f
                        }
                    }.start()
            } else {
                mFavoriteGroupH = mBinding.favoriteGroup.height
                ValueAnimator
                    .ofInt(mFavoriteGroupH, 0)
                    .setDuration(400)
                    .apply {
                        addUpdateListener {
                            mBinding.favoriteGroup.layoutParams.apply {
                                height = it.animatedValue as Int
                                if (height == 0) {
                                    mBinding.favoriteGroup.visibility = View.GONE
                                }
                            }
                            mBinding.favoriteGroup.requestLayout()
                            mBinding.favoriteDeleteTip.alpha = it.animatedValue as Int / mFavoriteGroupH.toFloat()
                            mBinding.expandFavorite.rotation = 180 - (it.animatedValue as Int / mFavoriteGroupH.toFloat()) * 180f
                        }
                    }.start()

            }
        }
    }

    private fun reset() {
        mBinding.favoriteGroup.clearCheck()
        mStatus = Status.SELECT_BOARD
        mBinding.fatherBoardsGroup.visibility = View.GONE
        mBinding.fatherBoardTitle.visibility = View.GONE
        mBinding.childBoardTitle.visibility = View.GONE
        mBinding.childBoardsGroup.visibility = View.GONE
        mBinding.classificationBoardTitle.visibility = View.GONE
        mBinding.classificationBoardsGroup.visibility = View.GONE
        mBinding.confirmBtn.visibility = View.GONE
        mBinding.mainBoardsGroup.clearCheck()
    }

    override fun onGetMainBoardListSuccess(forumListBean: ForumListBean) {
        mBinding.statusView.success()
        forumListBean.list?.forEach { main ->
            val chip = getChip(main.board_category_name)
            chip.tag = main.board_list
            chip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mBinding.fatherBoardsGroup.removeAllViews()
                mBinding.childBoardTitle.visibility = View.GONE
                mBinding.childBoardsGroup.visibility = View.GONE
                mBinding.classificationBoardTitle.visibility = View.GONE
                mBinding.classificationBoardsGroup.visibility = View.GONE
                mBinding.confirmBtn.visibility = View.GONE

                mSelectBoardResultEvent.boardCategoryId = main?.board_category_id
                mSelectBoardResultEvent.boardCategoryName = main?.board_category_name

                main.board_list?.forEach { father ->
                    val fatherChip = getChip(father.board_name)
                    fatherChip.setOnClickListener { v ->
                        v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        mBinding.classificationBoardTitle.visibility = View.GONE
                        mBinding.classificationBoardsGroup.visibility = View.GONE
                        mBinding.childBoardTitle.visibility = View.GONE
                        mBinding.childBoardsGroup.visibility = View.GONE
                        mBinding.confirmBtn.visibility = View.GONE

                        mSelectBoardResultEvent.fatherBoardId = father?.board_id
                        mSelectBoardResultEvent.fatherBName = father?.board_name

                        mPresenter?.getChildBoardList(mSelectBoardResultEvent.fatherBoardId!!, mSelectBoardResultEvent.fatherBName!!)
                    }
                    mBinding.fatherBoardTitle.visibility = View.VISIBLE
                    mBinding.fatherBoardsGroup.visibility = View.VISIBLE
                    mBinding.fatherBoardsGroup.addView(fatherChip)
                }
            }

            mBinding.mainBoardsGroup.addView(chip)
        }
    }

    override fun onGetMainBoardListError(msg: String?) {
        mBinding.statusView.error()
        showToast("加载版块失败:$msg", ToastType.TYPE_ERROR)
    }

    override fun onGetChildBoardListSuccess(subForumListBean: SubForumListBean) {
        mBinding.childBoardsGroup.removeAllViews()
        mBinding.childBoardTitle.visibility = View.VISIBLE
        mBinding.childBoardsGroup.visibility = View.VISIBLE
        subForumListBean.list?.getOrNull(0)?.board_list?.forEach { child ->
            val childChip = getChip(child.board_name)
            childChip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mBinding.confirmBtn.visibility = View.GONE
                mBinding.classificationBoardTitle.visibility = View.GONE
                mBinding.classificationBoardsGroup.visibility = View.GONE
                mSelectBoardResultEvent.childBoardId = child?.board_id
                mSelectBoardResultEvent.childBoardName= child?.board_name
                mSelectBoardResultEvent.childBoardId?.let { mPresenter?.getClassification(it) }
            }
            mBinding.childBoardsGroup.addView(childChip)
        }
    }

    override fun onGetChildBoardListError(msg: String?) {
        showToast("加载版块失败:$msg", ToastType.TYPE_ERROR)
    }

    override fun onGetClassificationSuccess(classifications: List<CommonPostBean.ClassificationTypeListBean>) {
        setClassifications(classifications)
    }

    override fun onGetClassificationError(msg: String?,
                                          classifications: List<CommonPostBean.ClassificationTypeListBean>) {
        showToast("加载分类失败，请选择不分类:$msg", ToastType.TYPE_ERROR)
        setClassifications(classifications)
    }

    private fun setClassifications(classifications: List<CommonPostBean.ClassificationTypeListBean>) {
        mBinding.classificationBoardsGroup.removeAllViews()
        mBinding.classificationBoardTitle.visibility = View.VISIBLE
        mBinding.classificationBoardsGroup.visibility = View.VISIBLE
        classifications.forEach { classification ->
            val classificationChip = getChip(classification.classificationType_name)
            classificationChip.setOnClickListener { v ->
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mSelectBoardResultEvent.classificationId = classification.classificationType_id
                mSelectBoardResultEvent.classificationName= classification.classificationType_name

                if (mStatus == Status.SELECT_FAVORITE) {
                    if (mNeedConfirm) {
                        mBinding.confirmBtn.apply {
                            visibility = View.VISIBLE
                            text = "确认添加"
                        }
                    } else {
                        addFavoriteToDB()
                        initFavorite()
                        reset()
                    }
                } else {
                    if (mNeedConfirm) {
                        mBinding.confirmBtn.apply {
                            visibility = View.VISIBLE
                            text = "确认选择"
                        }
                    } else {
                        dismiss()
                    }
                }
            }
            mBinding.classificationBoardsGroup.addView(classificationChip)
        }
    }

    override fun setMaxHeightMultiplier() = 0.9

    override fun dismiss() {
        EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.BOARD_SELECTED, mSelectBoardResultEvent))
        super.dismiss()
    }

    private fun addFavoriteToDB() {
        val favoriteBean = SelectBoardFavoriteBean().apply {
            uid = SharePrefUtil.getUid(context)
            boardId = mSelectBoardResultEvent.childBoardId
            boardName = mSelectBoardResultEvent.childBoardName
            classificationId = mSelectBoardResultEvent.classificationId
            classificationName = mSelectBoardResultEvent.classificationName
        }
        favoriteBean.saveOrUpdate("boardId = ? and classificationId = ? and uid = ?",
            mSelectBoardResultEvent.childBoardId.toString(),
            mSelectBoardResultEvent.classificationId.toString(),
            SharePrefUtil.getUid(context).toString())
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

    private fun getAddFavoriteChip() = Chip(ContextThemeWrapper(context, R.style.Widget_Material3_Chip_Filter)).apply {
        text = "添加"
        isCheckable = true
        chipStrokeWidth = 0f
        chipIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_add_outline, context.theme)
        checkedIconTint = ColorStateList.valueOf(ColorUtil.getAttrColor(context, R.attr.colorPrimary))
        setChipBackgroundColorResource(R.color.select_board_chip_bg_color)
        shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            setAllCornerSizes(ScreenUtil.dip2pxF(requireContext(), 20f))
        }.build()
        chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#00000000"))
    }

    enum class Status {
        SELECT_BOARD, SELECT_FAVORITE
    }

}