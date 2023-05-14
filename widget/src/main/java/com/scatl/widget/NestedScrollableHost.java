package com.scatl.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

/**
 * created by sca_tl at 2022/9/18 9:27
 */
public class NestedScrollableHost extends FrameLayout {

    private ViewPager2 parentViewPager;
    private int touchSlop = 0;
    private float initialX = 0f;
    private float initialY = 0f;

    public NestedScrollableHost(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context,
                                @Nullable AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public NestedScrollableHost(@NonNull Context context,
                                @Nullable AttributeSet attrs,
                                int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                View v = (View) getParent();
                while (v != null && !(v instanceof ViewPager2)){
                    v = (View) v.getParent();
                }
                parentViewPager = (ViewPager2) v;

                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    public static View getChildScrollableView(View view) {
        ArrayList<View> unvisited = new ArrayList<>();
        unvisited.add(view);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (child instanceof RecyclerView
                    || child instanceof ViewPager2
                    || child instanceof ViewPager
                    || child instanceof HorizontalScrollView) {
                return child;
            }
            if (!(child instanceof ViewGroup)) {
                continue;
            }
            ViewGroup viewGroup = (ViewGroup) child;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                unvisited.add(viewGroup.getChildAt(i));
            }
        }
        return null;
    }

    private boolean canChildScroll(int orientation, float delta) {
        int direction = (int) -delta;
        View child = getChildScrollableView(this);
        if (child != null) {
            if (orientation == 0) {
                return child.canScrollHorizontally(direction);
            } else if (orientation == 1) {
                return child.canScrollVertically(direction);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return false;
    }

    private void handleInterceptTouchEvent(MotionEvent e) {
        if (parentViewPager == null) return;
        int orientation = parentViewPager.getOrientation();

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return;
        }

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = e.getX();
            initialY = e.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX()- initialX;
            float dy = e.getY() - initialY;
            boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            float scaledDx = Math.abs(dx) * (isVpHorizontal ?  .5f : 1f);
            float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : .5f);
            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, isVpHorizontal ? dx : dy)) {
                        // Child can scroll, disallow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
        }
    }
}


