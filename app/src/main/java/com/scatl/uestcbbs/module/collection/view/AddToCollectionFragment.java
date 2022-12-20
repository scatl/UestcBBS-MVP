package com.scatl.uestcbbs.module.collection.view;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.AddToCollectionBean;
import com.scatl.uestcbbs.module.collection.presenter.AddToCollectionPresenter;
import com.scatl.uestcbbs.util.AnimationUtil;
import com.scatl.uestcbbs.util.Constant;

import java.util.List;

public class AddToCollectionFragment extends BaseBottomFragment implements AddToCollectionView, AdapterView.OnItemSelectedListener{

    View addLayout, createLayout, layout;
    AppCompatSpinner spinner;
    TextInputEditText addReason, createTitle, createDesc, createKeyword;
    Button addConfirmBtn, createConfirmBtn;
    TextView hint, remainNum, createBtn, backBtn, title;
    LottieAnimationView loading;

    AddToCollectionPresenter addToCollectionPresenter;

    int tid, selectedCtid;
    List<AddToCollectionBean> addToCollectionBeanList;

    public static AddToCollectionFragment getInstance(Bundle bundle) {
        AddToCollectionFragment addToCollectionFragment = new AddToCollectionFragment();
        addToCollectionFragment.setArguments(bundle);
        return addToCollectionFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            tid = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_add_to_collection;
    }

    @Override
    protected void findView() {
        title = view.findViewById(R.id.add_to_collection_title);
        layout = view.findViewById(R.id.add_to_collection_layout);
        addLayout = view.findViewById(R.id.add_to_collection_add_layout);
        createLayout = view.findViewById(R.id.add_to_collection_create_layout);
        spinner = view.findViewById(R.id.add_to_collection_spinner);
        addReason = view.findViewById(R.id.add_to_collection_add_reason);
        createTitle = view.findViewById(R.id.add_to_collection_create_title);
        createDesc = view.findViewById(R.id.add_to_collection_create_desc);
        createKeyword = view.findViewById(R.id.add_to_collection_create_keyword);
        addConfirmBtn = view.findViewById(R.id.add_to_collection_confirm_btn);
        createConfirmBtn = view.findViewById(R.id.add_to_collection_create_confirm_btn);
        hint = view.findViewById(R.id.add_to_collection_hint);
        loading = view.findViewById(R.id.add_to_collection_loading);
        remainNum = view.findViewById(R.id.add_to_collection_remain_num);
        createBtn = view.findViewById(R.id.add_to_collection_create_collection_btn);
        backBtn = view.findViewById(R.id.add_to_collection_create_back_btn);
    }

    @Override
    protected void initView() {
        addToCollectionPresenter = (AddToCollectionPresenter) presenter;

        addLayout.setVisibility(View.GONE);
        createLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);

        createBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        addConfirmBtn.setOnClickListener(this);
        createConfirmBtn.setOnClickListener(this);

        addToCollectionPresenter.addToCollection(tid);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new AddToCollectionPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.add_to_collection_create_collection_btn) {
            createLayout.setVisibility(View.VISIBLE);
            addLayout.setVisibility(View.GONE);
            createLayout.setAnimation(AnimationUtil.showFromLeft());
            addLayout.setAnimation(AnimationUtil.hideToRight());
            title.setText("创建淘专辑");
        }
        if (view.getId() == R.id.add_to_collection_create_back_btn) {
            addLayout.setVisibility(View.VISIBLE);
            createLayout.setVisibility(View.GONE);
            addLayout.setAnimation(AnimationUtil.showFromRight());
            createLayout.setAnimation(AnimationUtil.hideToLeft());
            title.setText("添加到淘专辑");
        }
        if (view.getId() == R.id.add_to_collection_confirm_btn) {
            addToCollectionPresenter.confirmAddToCollection(addReason.getText().toString(), tid, selectedCtid);
        }
        if (view.getId() == R.id.add_to_collection_create_confirm_btn) {
            addToCollectionPresenter.createCollection(createTitle.getText().toString(), createDesc.getText().toString(), createKeyword.getText().toString());
        }
    }

    @Override
    public void onGetAddToCollectionSuccess(List<AddToCollectionBean> addToCollectionBeanList, int remainNUm) {
        hint.setText("");
        loading.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        addLayout.setVisibility(View.VISIBLE);

        if (remainNUm != 0) {
            remainNum.setText("您还可以创建" + remainNUm + "个淘专辑");
        } else {
            remainNum.setVisibility(View.GONE);
            createBtn.setVisibility(View.GONE);
        }

        this.addToCollectionBeanList = addToCollectionBeanList;
        String[] sItems = new String[addToCollectionBeanList.size()];
        for (int i = 0; i < addToCollectionBeanList.size(); i ++) {
            sItems[i] = addToCollectionBeanList.get(i).name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, sItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onNoneCollection(String msg) {
        hint.setText("");
        showToast("您还没有淘专辑，请创建", ToastType.TYPE_NORMAL);
        loading.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        createLayout.setVisibility(View.VISIBLE);
        addLayout.setVisibility(View.GONE);
        title.setText("创建淘专辑");
    }

    @Override
    public void onGetAddToCollectionError(String msg) {
        hint.setText(msg);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onConfirmAddToCollectionSuccess(String msg) {
        showToast(msg, ToastType.TYPE_SUCCESS);
        dismiss();
    }

    @Override
    public void onConfirmAddToCollectionError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onCreateCollectionSuccess(String msg) {
        showToast(msg, ToastType.TYPE_SUCCESS);
        addToCollectionPresenter.addToCollection(tid);
        addLayout.setVisibility(View.VISIBLE);
        createLayout.setVisibility(View.GONE);
        addLayout.setAnimation(AnimationUtil.showFromRight());
        createLayout.setAnimation(AnimationUtil.hideToLeft());
        title.setText("添加到淘专辑");
    }

    @Override
    public void onCreateCollectionError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCtid = addToCollectionBeanList.get(position).ctid;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}