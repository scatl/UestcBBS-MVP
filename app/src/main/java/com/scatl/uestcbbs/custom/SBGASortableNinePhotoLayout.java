package com.scatl.uestcbbs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;

import java.util.ArrayList;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.baseadapter.BGAGridDivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGARecyclerViewHolder;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;

public class SBGASortableNinePhotoLayout extends RecyclerView implements BGAOnItemChildClickListener, BGAOnRVItemClickListener {

    private SBGASortableNinePhotoLayout.PhotoAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private OnClickListener mOnClickListener;
    private GridLayoutManager mGridLayoutManager;

    private boolean mPlusEnable;
    private boolean mSortable;
    private int mDeleteDrawableResId;
    private boolean mDeleteDrawableOverlapQuarter;
    private int mPhotoTopRightMargin;
    private int mMaxItemCount;
    private int mItemSpanCount;
    private int mPlusDrawableResId;
    private int mItemCornerRadius;
    private int mItemWhiteSpacing;
    private int mOtherWhiteSpacing;
    private int mPlaceholderDrawableResId;
    private boolean mEditable;

    private boolean mShowAsLargeWhenOnlyOne;

    private int mItemWidth;

    private int mMaxItemDisplayedBeforeExpand;
    private boolean mIsExpand;

    private Drawable mExpandMaskBackground;
    private int mExpandMaskTextColor;

    public SBGASortableNinePhotoLayout(Context context) {
        this(context, null);
    }

    public SBGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBGASortableNinePhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
        afterInitDefaultAndCustomAttrs();
    }

    private void initDefaultAttrs() {
        mPlusEnable = true;
        mSortable = true;
        mEditable = true;
        mDeleteDrawableResId = cn.bingoogolapple.photopicker.R.mipmap.bga_pp_ic_delete;
        mDeleteDrawableOverlapQuarter = false;
        mMaxItemCount = 9;
        mItemSpanCount = 3;
        mItemWidth = 0;
        mItemCornerRadius = 0;
        mPlusDrawableResId = cn.bingoogolapple.photopicker.R.mipmap.bga_pp_ic_plus;
        mItemWhiteSpacing = BGABaseAdapterUtil.dp2px(4);
        mPlaceholderDrawableResId = cn.bingoogolapple.photopicker.R.mipmap.bga_pp_ic_holder_light;
        mOtherWhiteSpacing = BGABaseAdapterUtil.dp2px(100);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SBGASortableNinePhotoLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_plusEnable) {
            mPlusEnable = typedArray.getBoolean(attr, mPlusEnable);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_sortable) {
            mSortable = typedArray.getBoolean(attr, mSortable);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_deleteDrawable) {
            mDeleteDrawableResId = typedArray.getResourceId(attr, mDeleteDrawableResId);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_deleteDrawableOverlapQuarter) {
            mDeleteDrawableOverlapQuarter = typedArray.getBoolean(attr, mDeleteDrawableOverlapQuarter);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_maxItemCount) {
            mMaxItemCount = typedArray.getInteger(attr, mMaxItemCount);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_itemSpanCount) {
            mItemSpanCount = typedArray.getInteger(attr, mItemSpanCount);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_plusDrawable) {
            mPlusDrawableResId = typedArray.getResourceId(attr, mPlusDrawableResId);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_itemCornerRadius) {
            mItemCornerRadius = typedArray.getDimensionPixelSize(attr, 0);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_itemWhiteSpacing) {
            mItemWhiteSpacing = typedArray.getDimensionPixelSize(attr, mItemWhiteSpacing);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_otherWhiteSpacing) {
            mOtherWhiteSpacing = typedArray.getDimensionPixelOffset(attr, mOtherWhiteSpacing);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_placeholderDrawable) {
            mPlaceholderDrawableResId = typedArray.getResourceId(attr, mPlaceholderDrawableResId);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_editable) {
            mEditable = typedArray.getBoolean(attr, mEditable);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_itemWidth) {
            mItemWidth = typedArray.getDimensionPixelSize(attr, mItemWidth);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_isExpand) {
            mIsExpand = typedArray.getBoolean(attr, mIsExpand);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_maxItemDisplayBeforeExpand) {
            int max = typedArray.getInteger(attr, mMaxItemDisplayedBeforeExpand);
            if (max < 0) {
                throw new IllegalArgumentException("Max Item Displayed Before Expand cannot be negative!");
            } else {
                mMaxItemDisplayedBeforeExpand = max;
            }
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_maskBackground) {
            mExpandMaskBackground = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_maskTextColor) {
            mExpandMaskTextColor = typedArray.getColor(attr, mExpandMaskTextColor);
        } else if (attr == R.styleable.SBGASortableNinePhotoLayout_bga_snpl_showAsLargeWhenOnlyOne) {
            mShowAsLargeWhenOnlyOne = typedArray.getBoolean(attr, mShowAsLargeWhenOnlyOne);
        }
    }

    private void afterInitDefaultAndCustomAttrs() {
        if (mItemWidth == 0) {
            mItemWidth = (BGAPhotoPickerUtil.getScreenWidth() - mOtherWhiteSpacing) / mItemSpanCount;
        } else {
            mItemWidth += mItemWhiteSpacing;
        }

        setOverScrollMode(OVER_SCROLL_NEVER);
        mItemTouchHelper = new ItemTouchHelper(new SBGASortableNinePhotoLayout.ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(getContext(), mItemSpanCount);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(BGAGridDivider.newInstanceWithSpacePx(mItemWhiteSpacing / 2));

        calculatePhotoTopRightMargin();

        mPhotoAdapter = new SBGASortableNinePhotoLayout.PhotoAdapter(this);
        mPhotoAdapter.setOnItemChildClickListener(this);
        mPhotoAdapter.setOnRVItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    /**
     * 设置是否可拖拽排序，默认值为 true
     *
     * @param sortable
     */
    public void setSortable(boolean sortable) {
        mSortable = sortable;
    }

    /**
     * 获取是否可拖拽排序
     *
     * @return
     */
    public boolean isSortable() {
        return mSortable;
    }

    /**
     * 设置是否可编辑，默认值为 true
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        mEditable = editable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 获取是否可编辑
     *
     * @return
     */
    public boolean isEditable() {
        return mEditable;
    }

    /**
     * 设置删除按钮图片资源id，默认值为
     *
     * @param deleteDrawableResId
     */
    public void setDeleteDrawableResId(@DrawableRes int deleteDrawableResId) {
        mDeleteDrawableResId = deleteDrawableResId;
        calculatePhotoTopRightMargin();
    }

    /**
     * 设置删除按钮是否重叠四分之一，默认值为 false
     *
     * @param deleteDrawableOverlapQuarter
     */
    public void setDeleteDrawableOverlapQuarter(boolean deleteDrawableOverlapQuarter) {
        mDeleteDrawableOverlapQuarter = deleteDrawableOverlapQuarter;
        calculatePhotoTopRightMargin();
    }

    /**
     * 计算图片右上角 margin
     */
    private void calculatePhotoTopRightMargin() {
        if (mDeleteDrawableOverlapQuarter) {
            int deleteDrawableWidth = BitmapFactory.decodeResource(getResources(), mDeleteDrawableResId).getWidth();
            int deleteDrawablePadding = getResources().getDimensionPixelOffset(cn.bingoogolapple.photopicker.R.dimen.bga_pp_size_delete_padding);
            mPhotoTopRightMargin = deleteDrawablePadding + deleteDrawableWidth / 2;
        } else {
            mPhotoTopRightMargin = 0;
        }
    }

    /**
     * 设置可选择图片的总张数,默认值为 9
     *
     * @param maxItemCount
     */
    public void setMaxItemCount(int maxItemCount) {
        mMaxItemCount = maxItemCount;
    }

    /**
     * 获取选择的图片的最大张数
     *
     * @return
     */
    public int getMaxItemCount() {
        return mMaxItemCount;
    }

    /**
     * 设置列数,默认值为 3
     *
     * @param itemSpanCount
     */
    public void setItemSpanCount(int itemSpanCount) {
        mItemSpanCount = itemSpanCount;
        mGridLayoutManager.setSpanCount(mItemSpanCount);
    }

    /**
     * 设置添加按钮图片，默认值为 R.mipmap.bga_pp_ic_plus
     *
     * @param plusDrawableResId
     */
    public void setPlusDrawableResId(@DrawableRes int plusDrawableResId) {
        mPlusDrawableResId = plusDrawableResId;
    }

    /**
     * 设置 Item 条目圆角尺寸，默认值为 0dp
     *
     * @param itemCornerRadius
     */
    public void setItemCornerRadius(int itemCornerRadius) {
        mItemCornerRadius = itemCornerRadius;
    }

    /**
     * 设置图片路径数据集合
     *
     * @param photos
     */
    public void setData(ArrayList<String> photos) {
        mPhotoAdapter.setData(photos);
    }

    /**
     * 在集合尾部添加更多数据集合
     *
     * @param photos
     */
    public void addMoreData(ArrayList<String> photos) {
        if (photos != null) {
            mPhotoAdapter.getData().addAll(photos);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 在集合的尾部添加一条数据
     *
     * @param photo
     */
    public void addLastItem(String photo) {
        if (!TextUtils.isEmpty(photo)) {
            mPhotoAdapter.getData().add(photo);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spanCount = mItemSpanCount;
        int itemCount = mPhotoAdapter.getItemCount();
        if (itemCount > 0 && itemCount < mItemSpanCount) {
            spanCount = itemCount;
        }
        mGridLayoutManager.setSpanCount(spanCount);

        int expectWidth = mItemWidth * spanCount;
        int expectHeight = 0;
        if (itemCount > 0) {
            int rowCount = (itemCount - 1) / spanCount + 1;
            expectHeight = mItemWidth * rowCount;
        }

        int width = resolveSize(expectWidth, widthMeasureSpec);
        int height = resolveSize(expectHeight, heightMeasureSpec);
        width = Math.min(width, expectWidth);
        height = Math.min(height, expectHeight);

        setMeasuredDimension(width, height);
    }

    /**
     * 获取图片路径数据集合
     *
     * @return
     */
    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    /**
     * 删除指定索引位置的图片
     *
     * @param position
     */
    public void removeItem(int position) {
        mPhotoAdapter.removeItem(position);
    }

    /**
     * 获取图片总数
     *
     * @return
     */
    public int getItemCount() {
        return mPhotoAdapter.getData().size();
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (mOnClickListener != null) {
            //mDelegate.onClickDeleteNinePhotoItem(this, childView, position, mPhotoAdapter.getItem(position), getData());
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (mPhotoAdapter.isPlusItem(position)) {
            if (mOnClickListener != null) {
                //mDelegate.onClickAddNinePhotoItem(this, itemView, position, getData());
            }
        } else {
            if (mOnClickListener != null && ViewCompat.getScaleX(itemView) <= 1.0f) {
                mOnClickListener.onClickNinePhotoItem(this, itemView, position, mPhotoAdapter.getItem(position), getData());
            }
        }
    }

    /**
     * 设置是否显示加号
     *
     * @param plusEnable
     */
    public void setPlusEnable(boolean plusEnable) {
        mPlusEnable = plusEnable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 是否显示加号按钮
     *
     * @return
     */
    public boolean isPlusEnable() {
        return mPlusEnable;
    }

    public void setDelegate(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    private class PhotoAdapter extends BGARecyclerViewAdapter<String> {
        private int mImageSize;

        public PhotoAdapter(RecyclerView recyclerView) {
            super(recyclerView, cn.bingoogolapple.photopicker.R.layout.bga_pp_item_nine_photo);
            mImageSize = BGAPhotoPickerUtil.getScreenWidth() / (mItemSpanCount > 3 ? 8 : 6);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
            helper.setItemChildClickListener(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_flag);
        }

        @Override
        public int getItemCount() {
            return mIsExpand || mData.size() <= mMaxItemDisplayedBeforeExpand ?
                    super.getItemCount() : mData.subList(0, mMaxItemDisplayedBeforeExpand).size();

        }

        @Override
        public String getItem(int position) {
            if (isPlusItem(position)) {
                return null;
            }

            return super.getItem(position);
        }

        private void displayExpandMaskIfNeed(BGAViewHolderHelper helper, int position) {
            TextView maskTv = helper.getView(cn.bingoogolapple.photopicker.R.id.tv_expand_remain_mask);
            maskTv.setBackground(mExpandMaskBackground);
            maskTv.setTextColor(Color.parseColor("#ffffff"));
            int remain = mData.size() - mMaxItemDisplayedBeforeExpand;
            if (remain > 0 && !mIsExpand && position == mMaxItemDisplayedBeforeExpand - 1) {
                maskTv.setVisibility(VISIBLE);
                String remainText = maskTv.getContext().getString(cn.bingoogolapple.photopicker.R.string.bga_pp_format_remain_image, remain);
                maskTv.setText(remainText);
            } else {
                maskTv.setVisibility(GONE);
            }

        }

        public boolean isPlusItem(int position) {
            return mEditable && mPlusEnable && super.getItemCount() < mMaxItemCount && position == getItemCount() - 1;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            MarginLayoutParams params = (MarginLayoutParams) helper.getView(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo).getLayoutParams();
            params.setMargins(0, mPhotoTopRightMargin, mPhotoTopRightMargin, 0);

            if (mItemCornerRadius > 0) {
                BGAImageView imageView = helper.getView(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo);
                imageView.setCornerRadius(mItemCornerRadius);
            }
            displayExpandMaskIfNeed(helper, position);
            if (isPlusItem(position)) {
                helper.setVisibility(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_flag, View.GONE);
                helper.setImageResource(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo, mPlusDrawableResId);
            } else {
                if (mEditable) {
                    helper.setVisibility(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_flag, View.VISIBLE);
                    helper.setImageResource(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_flag, mDeleteDrawableResId);
                } else {
                    helper.setVisibility(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_flag, View.GONE);
                }

                BGAImage.display(helper.getImageView(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo), mPlaceholderDrawableResId, model, mImageSize);
            }
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mEditable && mSortable && mPhotoAdapter.getData().size() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            if (mPhotoAdapter.isPlusItem(viewHolder.getAdapterPosition())) {
                dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            }
            int swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType() || mPhotoAdapter.isPlusItem(target.getAdapterPosition())) {
                return false;
            }
            mPhotoAdapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
            if (mOnClickListener != null) {
                //mDelegate.onNinePhotoItemExchanged(SBGASortableNinePhotoLayout.this, source.getAdapterPosition(), target.getAdapterPosition(), getData());
            }
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ACTION_STATE_IDLE) {
                ViewCompat.setScaleX(viewHolder.itemView, 1.2f);
                ViewCompat.setScaleY(viewHolder.itemView, 1.2f);
                ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo).setColorFilter(getResources().getColor(cn.bingoogolapple.photopicker.R.color.bga_pp_photo_selected_mask));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            ViewCompat.setScaleX(viewHolder.itemView, 1.0f);
            ViewCompat.setScaleY(viewHolder.itemView, 1.0f);
            ((BGARecyclerViewHolder) viewHolder).getViewHolderHelper().getImageView(cn.bingoogolapple.photopicker.R.id.iv_item_nine_photo_photo).setColorFilter(null);
            super.clearView(recyclerView, viewHolder);
        }
    }

    public interface OnClickListener {
        void onClickNinePhotoItem(SBGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models);
    }

}
