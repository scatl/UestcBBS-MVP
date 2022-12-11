package com.scatl.uestcbbs.module.user.view;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.user.adapter.AtUserViewPagerAdapter;

public class AtUserListActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_at_user_list;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.at_user_list_tab_layout);
        viewPager = findViewById(R.id.at_user_list_viewpager);
    }

    @Override
    protected void initView() {
        super.initView();
        viewPager.setAdapter(new AtUserViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0, true);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

}
