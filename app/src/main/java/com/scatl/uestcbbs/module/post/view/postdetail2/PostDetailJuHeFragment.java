package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseIndicatorAdapter;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.post.adapter.PostDetail2ViewPagerAdapter;
import com.scatl.uestcbbs.util.Constant;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import biz.laenger.android.vpbs.BottomSheetUtils;


public class PostDetailJuHeFragment extends BaseBottomFragment {

    ViewPager viewPager;
    MagicIndicator magicIndicator;

    int tid, pid;
    String formhash;
    int selected;

    public static PostDetailJuHeFragment getInstance(Bundle bundle) {
        PostDetailJuHeFragment postDetailJuHeFragment = new PostDetailJuHeFragment();
        postDetailJuHeFragment.setArguments(bundle);
        return postDetailJuHeFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
            pid = bundle.getInt(Constant.IntentKey.POST_ID, Integer.MAX_VALUE);
            selected = bundle.getInt(Constant.IntentKey.CURRENT_SELECT, 0);
            formhash = bundle.getString(Constant.IntentKey.FORM_HASH, "");
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_detail_ju_he;
    }

    @Override
    protected void findView() {
        viewPager = view.findViewById(R.id.post_detail_juhe_viewpager);
        magicIndicator = view.findViewById(R.id.post_detail_juhe_indicator);
    }

    @Override
    protected void initView() {
        BottomSheetUtils.setupViewPager(viewPager);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PostDetail2ViewPagerAdapter(getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tid, pid, formhash));

        String[] titles = new String[]{"热评", "评论", "点评", "点赞" , "打赏"};
        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new BaseIndicatorAdapter(titles, 15, viewPager));
        magicIndicator.setNavigator(commonNavigator);
        viewPager.setCurrentItem(selected);
        magicIndicator.onPageSelected(selected);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.9;
    }
}