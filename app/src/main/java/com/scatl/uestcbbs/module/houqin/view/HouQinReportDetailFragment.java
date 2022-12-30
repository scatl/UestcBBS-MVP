package com.scatl.uestcbbs.module.houqin.view;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseBottomFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.HouQinReportReplyBean;
import com.scatl.uestcbbs.entity.HouQinReportTopicBean;
import com.scatl.uestcbbs.module.houqin.adapter.HouQinReportTopicImageAdapter;
import com.scatl.uestcbbs.module.houqin.presenter.HouQinReportDetailPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.ImageUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class HouQinReportDetailFragment extends BaseBottomFragment implements HouQinReportDetailView{

    TextView topicContent, topicTitle, replyContent, hint;
    RecyclerView topicPicRv;
    HouQinReportTopicImageAdapter imageAdapter;
    View detailLayout;
    ProgressBar progressBar;

    HouQinReportDetailPresenter houQinReportDetailPresenter;

    int topicId;

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            topicId = bundle.getInt(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE);
        }
    }

    public static HouQinReportDetailFragment getInstance(Bundle bundle) {
        HouQinReportDetailFragment houQinReportDetailFragment = new HouQinReportDetailFragment();
        houQinReportDetailFragment.setArguments(bundle);
        return houQinReportDetailFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_hou_qin_report_detail;
    }

    @Override
    protected void findView() {
        topicContent = view.findViewById(R.id.fragment_houqin_report_detail_topic_content);
        topicPicRv = view.findViewById(R.id.fragment_houqin_report_detail_topic_pic_rv);
        topicTitle = view.findViewById(R.id.fragment_houqin_report_detail_topic_title);
        replyContent = view.findViewById(R.id.fragment_houqin_report_detail_reply_content);
        detailLayout = view.findViewById(R.id.fragment_houqin_report_detail_layout);
        hint = view.findViewById(R.id.fragment_houqin_report_detail_hint);
        progressBar = view.findViewById(R.id.fragment_houqin_report_detail_progressbar);
    }

    @Override
    protected void initView() {
        houQinReportDetailPresenter = (HouQinReportDetailPresenter) presenter;

        imageAdapter = new HouQinReportTopicImageAdapter(R.layout.item_post_create_comment_image);
        LinearLayoutManager linearLayoutManager = new MyLinearLayoutManger(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topicPicRv.setLayoutManager(linearLayoutManager);
        topicPicRv.setAdapter(imageAdapter);

        detailLayout.setVisibility(View.GONE);
        houQinReportDetailPresenter.getDetail(topicId);
    }

    @Override
    protected void setOnItemClickListener() {
        imageAdapter.setOnItemChildClickListener((adapter, view, position) ->
                ImageUtil.showImages(getContext(), imageAdapter.getData(), position));
    }

    @Override
    protected BasePresenter initPresenter() {
        return new HouQinReportDetailPresenter();
    }

    @Override
    public void onGetHouQinReportTopicSuccess(HouQinReportTopicBean houQinReportTopicBean) {

        List<String> imgs = new ArrayList<>();
        try {
            Document document = Jsoup.parse(houQinReportTopicBean.post.topic_text);
            for (int i = 0; i < document.select("img").size(); i ++) {
                imgs.add(ApiConstant.HOUQIN_BASE_URL + document.select("img").get(i).attr("src").replaceAll("\\\\", "/"));
            }

            mActivity.runOnUiThread(() -> {
                topicContent.setText(document.text());
                topicTitle.setText(houQinReportTopicBean.post.topic_title);
                imageAdapter.setNewData(imgs);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onGetHouQinReportReplySuccess(HouQinReportReplyBean houQinReportReplyBean) {
        try {
            Document document = Jsoup.parse(houQinReportReplyBean.replies.reply_text);
            replyContent.setText(document.text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        detailLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        hint.setText("");
    }

    @Override
    public void onGetReportDetailError(String msg) {
        hint.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected double setMaxHeightMultiplier() {
        return 0.92f;
    }
}