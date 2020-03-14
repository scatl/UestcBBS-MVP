package com.scatl.uestcbbs.module.user.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.module.user.view.AtUserListFragment;
import com.scatl.uestcbbs.module.user.view.UserAlbumFragment;
import com.scatl.uestcbbs.module.user.view.UserPostFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:05
 */
public class UserPostViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    private int uid;

    public UserPostViewPagerAdapter(@NonNull FragmentManager fm, int behavior, int uid) {
        super(fm, behavior);
        this.uid = uid;
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constant.IntentKey.TYPE, UserPostFragment.TYPE_USER_POST);
        bundle.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, UserPostFragment.TYPE_USER_REPLY);
        bundle1.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, UserPostFragment.TYPE_USER_FAVORITE);
        bundle2.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle2));

        Bundle bundle3 = new Bundle();
        bundle3.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserAlbumFragment.getInstance(bundle3));
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
