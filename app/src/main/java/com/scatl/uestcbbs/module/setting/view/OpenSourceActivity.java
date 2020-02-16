package com.scatl.uestcbbs.module.setting.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.OpenSourceBean;
import com.scatl.uestcbbs.module.setting.adapter.OpenSourceAdapter;
import com.scatl.uestcbbs.util.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OpenSourceActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OpenSourceAdapter openSourceAdapter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_open_source;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.open_source_toolbar);
        recyclerView = findViewById(R.id.open_source_rv);

    }

    @Override
    protected void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        openSourceAdapter = new OpenSourceAdapter( R.layout.item_open_source);
        openSourceAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(openSourceAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setData();
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void setOnItemClickListener() {
        openSourceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.item_open_source_cardview) {
                CommonUtil.openBrowser(OpenSourceActivity.this, openSourceAdapter.getData().get(position).link);
            }
        });
    }

    private void setData() {
        String data = "[]";
        InputStream is;
        try {
            is = getAssets().open("open_source_projects.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toString();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<OpenSourceBean> openSourceBeanList = JSONObject.parseArray(data, OpenSourceBean.class);
        openSourceAdapter.addData(openSourceBeanList);


    }
}
