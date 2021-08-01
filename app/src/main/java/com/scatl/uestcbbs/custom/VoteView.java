package com.scatl.uestcbbs.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * date: 2021/4/4 15:03
 * description: https://github.com/xuliangliang1992/VotingView
 */
public class VoteView extends View {

    /**
     * 左边的数量
     */
    private int leftNum;
    /**
     * 左边结束色
     */
    private int leftEndColor;
    /**
     * 左边开始色
     */
    private int leftStartColor;
    /**
     * 右边的数量
     */
    private int rightNum;
    /**
     * 右边开始色
     */
    private int rightStartColor;
    /**
     * 右边结束色
     */
    private int rightEndColor;
    /**
     * 白线倾斜度
     */
    private int mInclination;
    /**
     * 左边字体颜色
     */
    private int textColor;
    /**
     * 字体大小
     */
    private int textSize;
    /**
     * 左边边框距离
     */

    private Paint mPaint;
    private Path mPath;

    /**
     * 包含文字的框
     */
    private Rect mBound;

    public VoteView(Context context) {
        this(context, null);
        init();
    }

    public VoteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public VoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VoteView, defStyleAttr, 0);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();

        init();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.VoteView_leftNum) {
            leftNum = typedArray.getInteger(attr, 0);
        } else if (attr == R.styleable.VoteView_leftStartColor) {
            leftStartColor = typedArray.getColor(attr, Color.parseColor("#FF7566"));
        } else if (attr == R.styleable.VoteView_leftEndColor) {
            leftEndColor = typedArray.getColor(attr, Color.parseColor("#FFBD8D"));
        } else if (attr == R.styleable.VoteView_rightNum) {
            rightNum = typedArray.getInteger(attr, 0);
        } else if (attr == R.styleable.VoteView_rightStartColor) {
            rightStartColor = typedArray.getColor(attr, Color.parseColor("#13CCFF"));
        } else if (attr == R.styleable.VoteView_rightEndColor) {
            rightEndColor = typedArray.getColor(attr, Color.parseColor("#0091FF"));
        } else if (attr ==  R.styleable.VoteView_inclination) {
            mInclination = typedArray.getInteger(attr, 40);
        } else if (attr == R.styleable.VoteView_textColor) {
            textColor = typedArray.getColor(attr, Color.WHITE);
        } else if (attr == R.styleable.VoteView_textSize) {
            textSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getWidth() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingTop() + getHeight() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPath = new Path();
        mBound = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {

            float leftLength;
            float rightLength;
            //一边为0人时的最短距路
            float minLength = 10 + getHeight();
            if (leftNum != 0 && rightNum != 0) {
                //都不为零看比例
                leftLength = minLength + ((float) leftNum / (leftNum + rightNum)) * (getWidth() - 2 * minLength);
            } else {
                if (leftNum == 0) {
                    if (rightNum == 0) {
                        //左右都为0 各占一半
                        leftLength = (float) getWidth() / 2;
                    } else {
                        //左边为0右边不为0
                        leftLength = minLength;
                    }
                } else {
                    //左边不为0右边为0
                    rightLength = minLength;
                    leftLength = getWidth() - rightLength;
                }
            }

            //画半圆
            RectF leftOval = new RectF(0, 0, (float) getHeight(), (float) getHeight());
            mPath.moveTo((float) getHeight() / 2, (float) getHeight() / 2);
            mPath.arcTo(leftOval, 90, 180);

            //画矩形
            mPath.moveTo((float) getHeight() / 2, 0);
            mPath.lineTo(leftLength + mInclination - 5, 0);
            mPath.lineTo(leftLength - mInclination - 5, getHeight());
            mPath.lineTo((float) getHeight() / 2, getHeight());

            //渐变色
            Shader leftShader = new LinearGradient(0, 0, leftLength + mInclination - 5, 0, leftStartColor, leftEndColor, Shader.TileMode.CLAMP);
            mPaint.setShader(leftShader);

            //左边布局绘制完毕
            canvas.drawPath(mPath, mPaint);
            mPaint.setShader(null);
            mPath.reset();

            //画中间白线
            mPath.moveTo(leftLength + mInclination , 0);
            mPath.lineTo(leftLength + mInclination , 0);
            mPath.lineTo(leftLength - mInclination , getHeight());
            mPath.lineTo(leftLength - mInclination , getHeight());
            mPath.close();
            mPaint.setColor(Color.WHITE);
            canvas.drawPath(mPath, mPaint);
            mPath.reset();

            //画右边半圆
            RectF rightOval = new RectF((float) getWidth() - getHeight(), 0, (float) getWidth(), (float) getHeight());
            mPath.moveTo(getWidth() - (float)getHeight() / 2, (float) getHeight() / 2);
            mPath.arcTo(rightOval, -90, 180);
            //画右边矩形
            mPath.moveTo(leftLength + mInclination + 5, 0);
            mPath.lineTo(getWidth() - (float)getHeight() / 2, 0);
            mPath.lineTo(getWidth() - (float)getHeight() / 2, getHeight());
            mPath.lineTo(leftLength - mInclination + 5, getHeight());

            //渐变色
            Shader rightShader = new LinearGradient(leftLength + mInclination + 5, (float) getHeight(), (float) getWidth(), (float) getHeight(), rightStartColor, rightEndColor, Shader.TileMode.CLAMP);
            mPaint.setShader(rightShader);

            //右边布局绘制完毕
            canvas.drawPath(mPath, mPaint);
            mPaint.setShader(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String leftText = String.valueOf(leftNum);
//        String rightText = String.valueOf(rightNum);

//        mPaint.setColor(textColor);
//        mPaint.setTextSize(textSize);
//        mPaint.setTextAlign(Paint.Align.LEFT);
//        mPaint.getTextBounds(leftText, 0, leftText.length(), mBound);
//
//        //左边文字
//        canvas.drawText(leftText, getHeight() / 2 + 6, getHeight() / 2 - 36 + mBound.height() / 2, mPaint);
//        canvas.drawText("支持", getHeight() / 2 + 6, getHeight() / 2 + 12 + mBound.height() / 2, mPaint);
//
//        mPaint.setColor(textColor);
//        mPaint.getTextBounds(rightText, 0, rightText.length(), mBound);
//        mPaint.setTextAlign(Paint.Align.RIGHT);
//        //右边文字
//        canvas.drawText(rightText, getWidth() - getHeight() / 2 - 6, getHeight() / 2 - 36 + mBound.height() / 2, mPaint);
//        canvas.drawText("不支持", getWidth() - getHeight() / 2 - 6, getHeight() / 2 + 12 + mBound.height() / 2, mPaint);

    }

    /**
     * 动态设置
     *
     * @param leftNum  左边
     * @param rightNum 右边
     */
    public void setNum(int leftNum, int rightNum) {
        this.leftNum = leftNum;
        this.rightNum = rightNum;
        postInvalidate();
    }

    public int getLeftNum() {
        return leftNum;
    }

    public int getRightNum() {
        return rightNum;
    }

}
