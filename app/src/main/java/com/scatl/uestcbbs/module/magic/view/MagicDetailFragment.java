package com.scatl.uestcbbs.module.magic.view;

import android.graphics.Paint;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.magic.presenter.MagicDetailPresenter;
import com.scatl.uestcbbs.util.Constant;

public class MagicDetailFragment extends BaseBottomFragment implements MagicDetailView, RadioGroup.OnCheckedChangeListener{

    private ImageView icon;
    private TextView name, dsp, originalPrice, discountPrice, weight, stock, hint, otherInfo, availableWeight, buySuccessText;
    private ProgressBar progressBar;
    private View contentLayout;
    private Button buyBtn;
    private View buySuccessView;
    private RadioGroup radioGroup;
    private RadioButton count1, count3, count5, count10;

    MagicDetailPresenter magicDetailPresenter;

    String mid, formhash;
    int currentBuyCount;

    public static MagicDetailFragment getInstance(Bundle bundle) {
        MagicDetailFragment magicDetailFragment = new MagicDetailFragment();
        magicDetailFragment.setArguments(bundle);
        return magicDetailFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            mid = bundle.getString(Constant.IntentKey.MAGIC_ID);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_magic_detail;
    }

    @Override
    protected void findView() {
        icon = view.findViewById(R.id.magic_detail_fragment_icon);
        name = view.findViewById(R.id.magic_detail_fragment_name);
        dsp = view.findViewById(R.id.magic_detail_fragment_dsp);
        originalPrice = view.findViewById(R.id.magic_detail_fragment_original_price);
        discountPrice = view.findViewById(R.id.magic_detail_fragment_discount_price);
        weight = view.findViewById(R.id.magic_detail_fragment_weight);
        stock = view.findViewById(R.id.magic_detail_fragment_stock);
        progressBar = view.findViewById(R.id.magic_detail_fragment_progressbar);
        hint = view.findViewById(R.id.magic_detail_fragment_hint);
        contentLayout = view.findViewById(R.id.magic_detail_content_layout);
        otherInfo = view.findViewById(R.id.magic_detail_fragment_other_info);
        availableWeight = view.findViewById(R.id.magic_detail_fragment_available_weight);
        buyBtn = view.findViewById(R.id.magic_detail_fragment_buy_btn);
        buySuccessView = view.findViewById(R.id.magic_detail_fragment_buy_success_view);
        buySuccessText = view.findViewById(R.id.magic_detail_fragment_buy_success_text);
        count1 = view.findViewById(R.id.magic_detail_fragment_count_1);
        count3 = view.findViewById(R.id.magic_detail_fragment_count_3);
        count5 = view.findViewById(R.id.magic_detail_fragment_count_5);
        count10 = view.findViewById(R.id.magic_detail_fragment_count_10);
        radioGroup = view.findViewById(R.id.magic_detail_fragment_count_group);
    }

    @Override
    protected void initView() {
        magicDetailPresenter = (MagicDetailPresenter) presenter;
        count1.setChecked(true);
        currentBuyCount = 1;
        buyBtn.setText("购买" + currentBuyCount + "张");
        buyBtn.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        contentLayout.setVisibility(View.GONE);
        magicDetailPresenter.getMagicDetail(mid);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MagicDetailPresenter();
    }

    @Override
    protected void onClickListener(View v) {
        if (v.getId() == R.id.magic_detail_fragment_buy_btn) {
            buyBtn.setText("购买中，请稍候...");
            buyBtn.setEnabled(false);
            magicDetailPresenter.buyMagic(formhash, mid, currentBuyCount);
        }
    }

    @Override
    public void onGetMagicDetailSuccess(MagicDetailBean magicDetailBean, String formhash) {
        this.formhash = formhash;
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        hint.setText("");
        GlideLoader4Common.simpleLoad(mActivity, magicDetailBean.icon, icon);
        name.setText(magicDetailBean.name);
        dsp.setText(magicDetailBean.dsp);
        originalPrice.setText(String.format("原价：%s水滴", magicDetailBean.originalPrice));
        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        discountPrice.setText(String.format("折扣价：%s水滴",
                TextUtils.isEmpty(magicDetailBean.discountPrice) ? magicDetailBean.originalPrice : magicDetailBean.discountPrice));

        weight.setText(String.format("重量：%s", magicDetailBean.weight));
        stock.setText(String.format("库存：%s", magicDetailBean.stock));
        otherInfo.setText(magicDetailBean.otherInfo);
        availableWeight.setText(magicDetailBean.availableWeight);
    }

    @Override
    public void onGetMagicDetailError(String msg) {
        hint.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBuyMagicSuccess(String msg) {
        buySuccessView.setVisibility(View.VISIBLE);
        buySuccessText.setText(msg);
        contentLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBuyMagicError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
        buyBtn.setText("购买" + currentBuyCount + "张");
        buyBtn.setEnabled(true);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.magic_detail_fragment_count_1) {
            currentBuyCount = 1;
        } else if (checkedId == R.id.magic_detail_fragment_count_3) {
            currentBuyCount = 3;
        } else if (checkedId == R.id.magic_detail_fragment_count_5) {
            currentBuyCount = 5;
        } else if (checkedId == R.id.magic_detail_fragment_count_10) {
            currentBuyCount = 10;
        }
        buyBtn.setText("购买" + currentBuyCount + "张");
    }
}