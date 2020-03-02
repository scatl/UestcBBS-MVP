package com.scatl.uestcbbs.module.home.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scatl.uestcbbs.module.home.view.PostListFragment;
import com.scatl.uestcbbs.module.user.view.UserPostFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/2 13:54
 */
public class Home1ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public Home1ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, PostListFragment.TYPE_NEW);
        fragments.add(PostListFragment.getInstance(bundle1));

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, PostListFragment.TYPE_ALL);
        fragments.add(PostListFragment.getInstance(bundle));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, PostListFragment.TYPE_HOT);
        fragments.add(PostListFragment.getInstance(bundle2));
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
