package com.scatl.uestcbbs.module.main.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.module.board.view.BoardListFragment;
import com.scatl.uestcbbs.module.home.view.HomeMainFragment;
import com.scatl.uestcbbs.module.message.view.MessageFragment;
import com.scatl.uestcbbs.module.mine.view.MineFragment;

import java.util.ArrayList;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments;
    private FragmentActivity fragmentActivity;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        fragments.add(HomeMainFragment.getInstance(null));
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

//    https://stackoverflow.com/questions/57017226/how-to-fix-design-assumption-violated-error-in-viewpager2
//    @Override
//    public long getItemId(int position) {
//        return fragments.get(position).hashCode();
//    }

}
