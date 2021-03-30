package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;

public class P2DianZanFragment extends BaseFragment {

    public static P2DianZanFragment getInstance(Bundle bundle) {
        P2DianZanFragment p2DianZanFragment = new P2DianZanFragment();
        p2DianZanFragment.setArguments(bundle);
        return p2DianZanFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_p2_dian_zan;
    }

    @Override
    protected void findView() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}