package com.scatl.uestcbbs.module.credit.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.MineCreditBean;
import com.scatl.uestcbbs.module.board.view.BoardActivity;
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity;
import com.scatl.uestcbbs.module.collection.adapter.CollectionAdapter;
import com.scatl.uestcbbs.module.credit.adapter.MineCreditHistoryAdapter;
import com.scatl.uestcbbs.module.credit.presenter.MineCreditPresenter;
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionActivity;
import com.scatl.uestcbbs.module.houqin.view.HouQinReportListActivity;
import com.scatl.uestcbbs.module.magic.adapter.MineMagicAdapter;
import com.scatl.uestcbbs.module.magic.view.MagicShopActivity;
import com.scatl.uestcbbs.module.medal.view.MedalCenterActivity;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.w3c.dom.Text;

import java.util.List;

public class MineCreditActivity extends BaseActivity implements MineCreditView{

    TextView shuiDiTv, weiWangTv, jiangLiQuanTv, jiFenTv;

    Toolbar toolbar;
    MineCreditPresenter mineCreditPresenter;
    MineCreditHistoryAdapter mineCreditHistoryAdapter;
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    View viewMoreHistory;

    CardView dayQuestionCard, shuiDiTaskCard, exchangeCard, medalCenterCard, magicShopCard, shuiDiTransferCard;

    String formHash;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_mine_credit;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.mine_credit_toolbar);
        shuiDiTv = findViewById(R.id.mine_credit_shuidi_num);
        weiWangTv = findViewById(R.id.mine_credit_weiwang_num);
        jiangLiQuanTv = findViewById(R.id.mine_credit_jiangliquan_num);
        jiFenTv = findViewById(R.id.mine_credit_credit_num);
        recyclerView = findViewById(R.id.mine_credit_rv);
        refreshLayout = findViewById(R.id.mine_credit_refresh);
        viewMoreHistory = findViewById(R.id.mine_credit_view_more_history);
        dayQuestionCard = findViewById(R.id.mine_credit_day_question_card);
        shuiDiTaskCard = findViewById(R.id.mine_credit_shuidi_task_card);
        exchangeCard = findViewById(R.id.mine_credit_exchange_card);
        medalCenterCard = findViewById(R.id.mine_credit_medal_center_card);
        magicShopCard = findViewById(R.id.mine_credit_magic_shop_card);
        shuiDiTransferCard = findViewById(R.id.mine_credit_shuidi_transfer_card);
    }

    @Override
    protected void initView() {
        mineCreditPresenter = (MineCreditPresenter) presenter;

        dayQuestionCard.setOnClickListener(this::onClickListener);
        shuiDiTaskCard.setOnClickListener(this::onClickListener);
        exchangeCard.setOnClickListener(this::onClickListener);
        medalCenterCard.setOnClickListener(this::onClickListener);
        magicShopCard.setOnClickListener(this::onClickListener);
        shuiDiTransferCard.setOnClickListener(this::onClickListener);
        viewMoreHistory.setOnClickListener(this::onClickListener);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mineCreditHistoryAdapter = new MineCreditHistoryAdapter(R.layout.item_credit_history);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));
        recyclerView.setAdapter(mineCreditHistoryAdapter);

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new MineCreditPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.mine_credit_shuidi_transfer_card) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.FORM_HASH, formHash);
            CreditTransferFragment.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.mine_credit_medal_center_card) {
            startActivity(new Intent(this, MedalCenterActivity.class));
        }

        if (view.getId() == R.id.mine_credit_magic_shop_card) {
            startActivity(new Intent(this, MagicShopActivity.class));
        }

        if (view.getId() == R.id.mine_credit_day_question_card){
            startActivity(new Intent(this, DayQuestionActivity.class));
        }
        if (view.getId() == R.id.mine_credit_view_more_history) {
            startActivity(new Intent(this, CreditHistoryActivity.class));
        }
        if (view.getId() == R.id.mine_credit_exchange_card) {
            Intent intent = new Intent(this, SingleBoardActivity.class);
            //intent.putExtra(Constant.IntentKey.BOARD_NAME, "河畔商城");
            intent.putExtra(Constant.IntentKey.BOARD_ID, 399);
            startActivity(intent);
        }
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mineCreditPresenter.getMineCredit();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }


    @Override
    public void onGetMineCreditSuccess(MineCreditBean mineCreditBean, String formHash) {
        this.formHash = formHash;
        shuiDiTv.setText(mineCreditBean.shuiDiNum);
        weiWangTv.setText(mineCreditBean.weiWangNum);
        jiangLiQuanTv.setText(mineCreditBean.jiangLiQuanNum);
        jiFenTv.setText(mineCreditBean.jiFenNum);

        refreshLayout.finishRefresh();
        recyclerView.scheduleLayoutAnimation();
        mineCreditHistoryAdapter.setNewData(mineCreditBean.historyBeans);

    }

}