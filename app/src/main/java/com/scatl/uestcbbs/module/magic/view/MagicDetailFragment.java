package com.scatl.uestcbbs.module.magic.view;

import android.graphics.Paint;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.MagicDetailBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.magic.presenter.MagicDetailPresenter;
import com.scatl.uestcbbs.util.Constant;

public class MagicDetailFragment extends BaseBottomFragment implements MagicDetailView{

    private ImageView icon;
    private TextView name, dsp, originalPrice, discountPrice, weight, stock, hint, otherInfo, availableWeight, buySuccessText;
    private ProgressBar progressBar;
    private View contentLayout;
    private Button buyBtn;
    private View buySuccessView;

    MagicDetailPresenter magicDetailPresenter;

    String mid, formhash;

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
    }

    @Override
    protected void initView() {
        magicDetailPresenter = (MagicDetailPresenter) presenter;

        buyBtn.setOnClickListener(this);
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
            magicDetailPresenter.buyMagic(formhash, mid);
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
        discountPrice.setText(String.format("折扣价：%s水滴", magicDetailBean.discountPrice));

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
        showToast(msg);
        buyBtn.setText("购买");
        buyBtn.setEnabled(true);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }
}