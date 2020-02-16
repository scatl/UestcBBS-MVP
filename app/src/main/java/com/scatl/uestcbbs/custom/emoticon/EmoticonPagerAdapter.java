package com.scatl.uestcbbs.custom.emoticon;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class EmoticonPagerAdapter extends PagerAdapter {
    private List<View> mViewList;

    public EmoticonPagerAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return (mViewList.get(position));
    }

    @Override
    public int getCount() {
        if (mViewList == null)
            return 0;
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
