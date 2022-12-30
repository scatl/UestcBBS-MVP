package com.scatl.uestcbbs.module.user.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scatl.uestcbbs.annotation.UserPostType;
import com.scatl.uestcbbs.module.user.view.UserMainPageFragment;
import com.scatl.uestcbbs.module.user.view.UserPostFragment;
import com.scatl.uestcbbs.util.Constant;

import java.util.ArrayList;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/4 16:05
 */
public class UserPostViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private int uid;

    public UserPostViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int uid) {
        super(fragmentActivity);
        this.uid = uid;
        init();
    }

    private void init() {
        fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserMainPageFragment.getInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.IntentKey.TYPE, UserPostType.TYPE_USER_POST);
        bundle1.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constant.IntentKey.TYPE, UserPostType.TYPE_USER_REPLY);
        bundle2.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle2));

        Bundle bundle3 = new Bundle();
        bundle3.putString(Constant.IntentKey.TYPE, UserPostType.TYPE_USER_FAVORITE);
        bundle3.putInt(Constant.IntentKey.USER_ID, uid);
        fragments.add(UserPostFragment.getInstance(bundle3));
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
