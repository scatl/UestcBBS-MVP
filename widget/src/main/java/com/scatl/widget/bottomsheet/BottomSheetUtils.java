package com.scatl.widget.bottomsheet;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * created by sca_tl at 2022/12/30 19:25
 */
public final class BottomSheetUtils {

    public static void setupViewPager(ViewPager viewPager) {
        final View bottomSheetParent = findBottomSheetParent(viewPager);
        if (bottomSheetParent != null) {
            viewPager.addOnPageChangeListener(new BottomSheetViewPagerListener(viewPager, bottomSheetParent));
        }
    }

    private static class BottomSheetViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
        private final ViewPager viewPager;
        private final ViewPagerBottomSheetBehavior<View> behavior;

        private BottomSheetViewPagerListener(ViewPager viewPager, View bottomSheetParent) {
            this.viewPager = viewPager;
            this.behavior = ViewPagerBottomSheetBehavior.from(bottomSheetParent);
        }

        @Override
        public void onPageSelected(int position) {
            viewPager.post(behavior::invalidateScrollingChild);
        }
    }

    private static View findBottomSheetParent(final View view) {
        View current = view;
        while (current != null) {
            final ViewGroup.LayoutParams params = current.getLayoutParams();
            if (params instanceof CoordinatorLayout.LayoutParams && ((CoordinatorLayout.LayoutParams) params).getBehavior() instanceof ViewPagerBottomSheetBehavior) {
                return current;
            }
            final ViewParent parent = current.getParent();
            current = parent == null || !(parent instanceof View) ? null : (View) parent;
        }
        return null;
    }

}

