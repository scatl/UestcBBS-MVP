package com.scatl.uestcbbs.module.home.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.home.view.PostListFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/12 14:36
 */
public class HomeMainViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments;

    public HomeMainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        fragments.add(HomeFragment.getInstance(null));

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, PostListFragment.TYPE_ALL);
        fragments.add(PostListFragment.getInstance(bundle));

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
