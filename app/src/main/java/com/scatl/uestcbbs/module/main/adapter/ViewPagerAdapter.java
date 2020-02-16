package com.scatl.uestcbbs.module.main.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.module.board.view.BoardListFragment;
import com.scatl.uestcbbs.module.home.view.HomeFragment;
import com.scatl.uestcbbs.module.message.view.MessageFragment;
import com.scatl.uestcbbs.module.mine.view.MineFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        init();
    }

    private void init() {
        fragments = new ArrayList<>();
        fragments.add(HomeFragment.getInstance(null));
        fragments.add(BoardListFragment.getInstance(null));
        fragments.add(MessageFragment.getInstance(null));
        fragments.add(MineFragment.getInstance(null));
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
