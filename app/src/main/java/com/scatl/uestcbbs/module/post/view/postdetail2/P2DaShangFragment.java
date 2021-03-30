package com.scatl.uestcbbs.module.post.view.postdetail2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;

public class P2DaShangFragment extends BaseFragment {

    public static P2DaShangFragment getInstance(Bundle bundle) {
        P2DaShangFragment p2DaShangFragment = new P2DaShangFragment();
        p2DaShangFragment.setArguments(bundle);
        return p2DaShangFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_p2_da_shang;
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