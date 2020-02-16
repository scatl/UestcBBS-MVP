package com.scatl.uestcbbs.module.board.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scatl.uestcbbs.module.board.view.BoardPostFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:05
 */
public class BoardPostViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    private int boardId;

    public BoardPostViewPagerAdapter(@NonNull FragmentManager fm, int behavior, int boardId) {
        super(fm, behavior);
        this.boardId = boardId;
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, BoardPostFragment.TYPE_NEW);
        bundle.putInt(Constant.IntentKey.BOARD_ID, boardId);
        bundle.putInt(Constant.IntentKey.FILTER_ID, 0);
        fragments.add(BoardPostFragment.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, BoardPostFragment.TYPE_ALL);
        bundle1.putInt(Constant.IntentKey.BOARD_ID, boardId);
        bundle1.putInt(Constant.IntentKey.FILTER_ID, 0);
        fragments.add(BoardPostFragment.getInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, BoardPostFragment.TYPE_ESSENCE);
        bundle2.putInt(Constant.IntentKey.BOARD_ID, boardId);
        bundle2.putInt(Constant.IntentKey.FILTER_ID, 0);
        fragments.add(BoardPostFragment.getInstance(bundle2));
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
