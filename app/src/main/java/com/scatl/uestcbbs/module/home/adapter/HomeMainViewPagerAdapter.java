package com.scatl.uestcbbs.module.home.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.annotation.PostSortByType;
import com.scatl.uestcbbs.module.home.view.CollectionListFragment;
import com.scatl.uestcbbs.module.home.view.GrabSofaFragment;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.home.view.PostListFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/12 14:36
 */
public class HomeMainViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments;

    public HomeMainViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        fragments.add(HomeFragment.getInstance(null));

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, PostSortByType.TYPE_ALL);
        fragments.add(PostListFragment.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, PostSortByType.TYPE_HOT);
        fragments.add(PostListFragment.getInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, PostSortByType.TYPE_ESSENCE);
        fragments.add(PostListFragment.getInstance(bundle2));

        fragments.add(CollectionListFragment.getInstance(null));
        fragments.add(GrabSofaFragment.getInstance(null));

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
