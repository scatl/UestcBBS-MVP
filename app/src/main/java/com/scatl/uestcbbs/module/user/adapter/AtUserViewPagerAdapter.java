package com.scatl.uestcbbs.module.user.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.module.board.view.BoardListFragment;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.message.view.MessageFragment;
import com.scatl.uestcbbs.module.mine.view.MineFragment;
import com.scatl.uestcbbs.module.user.view.AtUserListFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class AtUserViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    private String[] titles = new String[]{"我的好友", "我关注的"};

    public AtUserViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        init();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
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

//    public AtUserViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//        init();
//    }
//
    private void init() {
        fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, AtUserListFragment.AT_LIST_TYPE_FRIEND);
        fragments.add(AtUserListFragment.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, AtUserListFragment.AT_LIST_TYPE_FOLLOW);
        fragments.add(AtUserListFragment.getInstance(bundle1));
    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return fragments.get(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return fragments.size();
//    }
}
