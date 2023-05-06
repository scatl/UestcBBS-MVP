package com.scatl.uestcbbs.module.home.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.module.collection.view.CollectionFragment;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.post.view.CommonPostFragment;
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
        bundle.putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_NEW_REPLY_POST);
        fragments.add(CommonPostFragment.Companion.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_HOT_POST);
        fragments.add(CommonPostFragment.Companion.getInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, CommonPostFragment.TYPE_ESSENCE_POST);
        fragments.add(CommonPostFragment.Companion.getInstance(bundle2));

        fragments.add(CollectionFragment.Companion.getInstance(null));

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
