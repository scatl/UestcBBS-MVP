package com.scatl.uestcbbs.module.board.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.annotation.PostSortByType;
import com.scatl.uestcbbs.module.board.view.BoardPostFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:05
 */
public class BoardPostViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private List<Integer> ids;

    public BoardPostViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Integer> ids) {
        super(fragmentActivity);
        this.ids = ids;
        init();
    }

    private void init() {
        fragments = new ArrayList<>();
        for (int i = 0; i < ids.size(); i ++) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.TYPE, PostSortByType.TYPE_ALL);
            bundle.putInt(Constant.IntentKey.BOARD_ID, ids.get(i));
            bundle.putInt(Constant.IntentKey.FILTER_ID, 0);
            fragments.add(BoardPostFragment.getInstance(bundle));
        }
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
