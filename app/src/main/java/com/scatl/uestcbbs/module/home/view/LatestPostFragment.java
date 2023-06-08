package com.scatl.uestcbbs.module.home.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.CommonPostBean;
import com.scatl.uestcbbs.manager.ForumListManager;
import com.scatl.uestcbbs.module.board.view.BoardActivity;
import com.scatl.uestcbbs.module.credit.view.CreditHistoryActivity;
import com.scatl.uestcbbs.module.credit.view.WaterTaskFragment;
import com.scatl.uestcbbs.module.darkroom.view.DarkRoomActivity;
import com.scatl.uestcbbs.module.post.adapter.CommonPostAdapter;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.BBSLinkUtil;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.BingPicBean;
import com.scatl.uestcbbs.entity.NoticeBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Banner;
import com.scatl.uestcbbs.callback.IHomeRefresh;
import com.scatl.uestcbbs.module.credit.view.CreditTransferFragment;
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionActivity;
import com.scatl.uestcbbs.module.home.presenter.LatestPostPresenter;
import com.scatl.uestcbbs.module.houqin.view.HouQinReportListActivity;
import com.scatl.uestcbbs.module.magic.view.MagicShopActivity;
import com.scatl.uestcbbs.module.medal.view.MedalCenterActivity;
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity;
import com.scatl.uestcbbs.module.user.view.UserDetailActivity;
import com.scatl.uestcbbs.services.DayQuestionService;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;
import com.scatl.util.ServiceUtil;
import com.scatl.widget.download.DownloadManager;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;

/**
 * author: sca_tl
 * description: 首页最新发表
 */
public class LatestPostFragment extends BaseFragment implements LatestPostView, IHomeRefresh {

    private static final String TAG = "LatestPostFragment";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CommonPostAdapter homeAdapter;

    private View bannerView, noticeView, gongGeView, topTopicView;
    private CardView noticeCard, marqueeCard;
    private GridViewPager gridViewPager;
    private Banner banner;
    private TextView noticeContent;
    private MarqueeView marqueeView;

    private LatestPostPresenter mLatestPostPresenter;

    private String imgUrl, imgCopyRight;

    private int total_post_page = 1;

    public static LatestPostFragment getInstance(Bundle bundle) {
        LatestPostFragment latestPostFragment = new LatestPostFragment();
        latestPostFragment.setArguments(bundle);
        return latestPostFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_latest_post;
    }

    @Override
    protected void findView() {

        mLatestPostPresenter = (LatestPostPresenter) presenter;
        recyclerView = view.findViewById(R.id.home_rv);
        refreshLayout = view.findViewById(R.id.home_refresh);

        bannerView = LayoutInflater.from(mActivity).inflate(R.layout.home_item_banner_view, new LinearLayout(mActivity));
        banner = bannerView.findViewById(R.id.home_item_banner_view_banner);

        gongGeView = LayoutInflater.from(mActivity).inflate(R.layout.home_item_gongge_view, new LinearLayout(mActivity));
        gridViewPager = gongGeView.findViewById(R.id.home_gongge_gridviewpager);

        noticeView = LayoutInflater.from(mActivity).inflate(R.layout.home_item_notice_view, new LinearLayout(mActivity));
        noticeContent = noticeView.findViewById(R.id.home_item_notice_view_content);
        noticeCard = noticeView.findViewById(R.id.home_item_notice_view_card);

        topTopicView = LayoutInflater.from(mActivity).inflate(R.layout.home_item_top_topic_view, new LinearLayout(mActivity));
        marqueeCard = topTopicView.findViewById(R.id.home_item_top_topic_layout);
        marqueeView = (MarqueeView) topTopicView.findViewById(R.id.home_item_top_topic_marquee_view);
    }

    @Override
    protected void initView() {

        homeAdapter = new CommonPostAdapter(R.layout.item_common_post, "", null);

        homeAdapter.addHeaderView(bannerView, 0);
        homeAdapter.addHeaderView(topTopicView, 1);
        homeAdapter.addHeaderView(noticeView, 2);
        homeAdapter.addHeaderView(gongGeView, 3);

        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(homeAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_scale_in));
        recyclerView.scheduleLayoutAnimation();

        initSavedData();
        initGonggeView();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        refreshLayout.autoRefresh(10, 300, 1, false);
    }

    @Override
    protected BasePresenter initPresenter() {
        return new LatestPostPresenter();
    }

    /**
     * author: sca_tl
     * description: 展示已保存的数据
     */
    private void initSavedData() {
        try {
            String homeBannerData = FileUtil.readTextFile(
                    new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME_BANNER_JSON));
            if (JSONObject.isValidObject(homeBannerData)) {
                JSONObject jsonObject = JSONObject.parseObject(homeBannerData);
                BingPicBean bingPicBean = JSONObject.toJavaObject(jsonObject, BingPicBean.class);
                initBannerView(bingPicBean);
            }

            String homeSimplePostData = FileUtil.readTextFile(
                    new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME_SIMPLE_POST_JSON));
            if (JSONObject.isValidObject(homeSimplePostData)) {
                JSONObject jsonObject = JSONObject.parseObject(homeSimplePostData);
                CommonPostBean simplePostListBean = JSONObject.toJavaObject(jsonObject, CommonPostBean.class);
                homeAdapter.setNewData(simplePostListBean.list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setOnItemClickListener() {
        homeAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.board_name) {
                Intent intent = new Intent(mActivity, BoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, ForumListManager.Companion.getINSTANCE().getParentForum(homeAdapter.getData().get(position).board_id).getId());
                intent.putExtra(Constant.IntentKey.LOCATE_BOARD_ID, homeAdapter.getData().get(position).board_id);
                intent.putExtra(Constant.IntentKey.BOARD_NAME, homeAdapter.getData().get(position).board_name);
                startActivity(intent);
            }
            if (view1.getId() == R.id.avatar) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra(Constant.IntentKey.USER_ID, homeAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
            if (view1.getId() == R.id.content_layout) {
                Intent intent = new Intent(mActivity, NewPostDetailActivity.class);
                intent.putExtra(Constant.IntentKey.TOPIC_ID, homeAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                total_post_page = 1;

                mLatestPostPresenter.getHomePage();
                mLatestPostPresenter.getBannerData();
                mLatestPostPresenter.getNotice();
                mLatestPostPresenter.getSimplePostList(1, SharePrefUtil.getPageSize(mActivity), "new", mActivity);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mLatestPostPresenter.getSimplePostList(total_post_page, SharePrefUtil.getPageSize(mActivity), "new", mActivity);
            }
        });
    }

    /**
     * author: sca_tl
     * description: banner
     */
    private void initBannerView(BingPicBean bingPicBean) {
        List<String> imgUrls = new ArrayList<>();
        List<String> imgTitles = new ArrayList<>();

        for (int i = 0; i < bingPicBean.images.size(); i ++) {
            imgUrls.add(ApiConstant.BING_BASE_URL + bingPicBean.images.get(i).url);
            imgTitles.add(bingPicBean.images.get(i).copyright);
        }

        banner
                .setImages(imgUrls)
                .setBannerTitles(imgTitles)
                .setImageLoader(new GlideLoader4Banner())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setDelayTime(3000)
                .setOnBannerListener(position -> {
                    this.imgUrl = imgUrls.get(position);
                    this.imgCopyRight = imgTitles.get(position);
                    DownloadManager
                            .Companion
                            .with(getContext())
                            .setTitle("必应每日一图高清图片下载")
                            .setUrl(imgUrl.replaceFirst("1920x1080", "UHD"))
                            .setName(imgCopyRight.replace("/", "_") + ".jpg")
                            .start();
                })
                .start();
        bannerView.setVisibility(SharePrefUtil.isShowHomeBanner(mActivity) ? View.VISIBLE : View.GONE);
    }

    private void initGonggeView() {
        String[] titles = {"每日答题", "失物招领","交通指南", "部门直通车","水滴小任务",
                "勋章中心","道具商店", "后勤投诉","在线用户","水滴转账", "积分记录", "新手导航", "小黑屋"};
        int[] iconS = {R.drawable.ic_hot, R.drawable.ic_lost_and_found,R.drawable.ic_timetable,
                R.drawable.ic_department,R.drawable.ic_task, R.drawable.ic_xunzhang,
                R.drawable.ic_magic, R.drawable.ic_report1,R.drawable.ic_huiyuan,
                R.drawable.ic_transfer, R.drawable.ic_integral, R.drawable.ic_daohang, R.drawable.ic_black_list1};
        ColorStateList[] colorStateLists = {
                ColorStateList.valueOf(Color.parseColor("#ff9090")),
                ColorStateList.valueOf(Color.parseColor("#90caf9")),
                ColorStateList.valueOf(Color.parseColor("#80deea")),
                ColorStateList.valueOf(Color.parseColor("#E3B0E2")),
                ColorStateList.valueOf(Color.parseColor("#59B2D1")),
                ColorStateList.valueOf(Color.parseColor("#C9A6D1")),
                ColorStateList.valueOf(Color.parseColor("#FF9C87")),
                ColorStateList.valueOf(Color.parseColor("#FF7D7F")),
                ColorStateList.valueOf(Color.parseColor("#B8A6FF")),
                ColorStateList.valueOf(Color.parseColor("#0BBCB3")),
                ColorStateList.valueOf(Color.parseColor("#4BB3FF")),
                ColorStateList.valueOf(Color.parseColor("#BA76C6")),
                ColorStateList.valueOf(Color.parseColor("#CC884C")),
        };

        gridViewPager
                .setDataAllCount(titles.length)
                .setImageTextLoaderInterface((imageView, textView, position) -> {
                    imageView.setImageResource(iconS[position]);
                    imageView.setImageTintList(colorStateLists[position]);
                    textView.setText(titles[position]);
                    textView.setTextSize(12f);
                    textView.setTextColor(colorStateLists[position]);
                })
                .setGridItemClickListener(position -> {
                    switch (position) {
                        case 0:
                            if (!ServiceUtil.isServiceRunning(mActivity, DayQuestionService.class.getName())) {
                                startActivity(new Intent(mActivity, DayQuestionActivity.class));
                            } else {
                                showToast("后台答题中", ToastType.TYPE_ERROR);
                            }
                            break;
                        case 1:
                            Intent intent = new Intent(mActivity, BoardActivity.class);
                            intent.putExtra(Constant.IntentKey.BOARD_ID, Constant.LOST_FOUND_BOARD_ID);
                            intent.putExtra(Constant.IntentKey.BOARD_NAME, Constant.LOST_FOUND_BOARD_NAME);
                            startActivity(intent);
                            break;
                        case 2:
                            Intent intent1 = new Intent(mActivity, WebViewActivity.class);
                            intent1.putExtra(Constant.IntentKey.URL, Constant.BUS_TIME);
                            startActivity(intent1);
                            break;
                        case 3:
                            Intent intent2 = new Intent(mActivity, BoardActivity.class);
                            intent2.putExtra(Constant.IntentKey.BOARD_ID, Constant.DEPARTMENT_BOARD_ID);
                            intent2.putExtra(Constant.IntentKey.BOARD_NAME, Constant.DEPARTMENT_BOARD_NAME);
                            startActivity(intent2);
                            break;
                        case 4:
                            WaterTaskFragment.Companion.getInstance(null).show(getChildFragmentManager(), TimeUtil.getStringMs());
                            break;
                        case 5: startActivity(new Intent(mActivity, MedalCenterActivity.class)); break;
                        case 6: startActivity(new Intent(mActivity, MagicShopActivity.class)); break;
                        case 7: startActivity(new Intent(mActivity, HouQinReportListActivity.class)); break;
                        case 8: OnLineUserFragment.getInstance(null).show(getChildFragmentManager(), TimeUtil.getStringMs()); break;
                        case 9: CreditTransferFragment.getInstance(null).show(getChildFragmentManager(), TimeUtil.getStringMs()); break;
                        case 10: startActivity(new Intent(mActivity, CreditHistoryActivity.class)); break;
                        case 11:
                            Intent intent3 = new Intent(mActivity, NewPostDetailActivity.class);
                            intent3.putExtra(Constant.IntentKey.TOPIC_ID, 1821753);
                            startActivity(intent3);
                            break;
                        case 12:
                            startActivity(new Intent(mActivity, DarkRoomActivity.class));
                            break;
                    }
                })
                .show();
    }

    @Override
    public void getBannerDataSuccess(BingPicBean bingPicBean) {
        initBannerView(bingPicBean);

        //保存为json文件
        FileUtil.saveStringToFile(JSON.toJSONString(bingPicBean),
                new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                        Constant.FileName.HOME_BANNER_JSON));
    }

    @Override
    public void getSimplePostDataSuccess(CommonPostBean simplePostListBean) {
        total_post_page = total_post_page + 1;

        if (refreshLayout.getState() == RefreshState.Refreshing) {
            homeAdapter.addData(simplePostListBean.list, true);
        } else {
            homeAdapter.addData(simplePostListBean.list, false);
        }

        if (refreshLayout.getState() == RefreshState.Refreshing)
            refreshLayout.finishRefresh();
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore();
        }

        if (simplePostListBean.page == 1) {
            //保存为json文件
            FileUtil.saveStringToFile(JSON.toJSONString(simplePostListBean),
                    new File(mActivity.getExternalFilesDir(Constant.AppPath.JSON_PATH),
                            Constant.FileName.HOME_SIMPLE_POST_JSON));
        }
    }

    @Override
    public void getSimplePostDataError(String msg) {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        }
        if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore(false);
        }

        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetNoticeSuccess(NoticeBean noticeBean) {
        try {
            if (noticeBean.isValid) {
                noticeCard.setVisibility(View.VISIBLE);
                noticeContent.setText(noticeBean.content);
                noticeContent.setTextColor(Color.parseColor(noticeBean.color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetNoticeError(String msg) {
        noticeCard.setVisibility(View.GONE);
    }

    @Override
    public void onGetHomePageSuccess(String msg) {
        try {
            Document document = Jsoup.parse(msg);
            Elements elements = document.select("div[class=module cl xl xl1]").select("li").select("a[title]");
            List<String> titles = new ArrayList<>();
            List<Integer> tids = new ArrayList<>();
            for (int i = 0; i < elements.size(); i ++) {
                if (elements.get(i).html().contains("<font")) {
                    titles.add(elements.get(i).select("font").text());
                    tids.add(BBSLinkUtil.getLinkInfo(elements.get(i).attr("href")).getId());
                }
            }
            if (titles.size() > 0 && !SharePrefUtil.isCloseTopStickPost(mActivity)) {
                for (int i = 0; i < titles.size(); i ++) {
                    titles.set(i, "(" + (i + 1) + "/" + titles.size() + ")" + titles.get(i));
                }
                marqueeCard.setVisibility(View.VISIBLE);
                marqueeView.startWithList(titles);

                marqueeView.setOnItemClickListener((position, textView) -> {
                    Intent intent = new Intent(mActivity, NewPostDetailActivity.class);
                    intent.putExtra(Constant.IntentKey.TOPIC_ID, tids.get(position));
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.HOME_BANNER_VISIBILITY_CHANGE) {
            recyclerView.scrollToPosition(0);
            refreshLayout.autoRefresh(10, 300, 1, false);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.ALL_SITE_TOP_STICK_VISIBILITY_CHANGE ) {
            marqueeCard.setVisibility(marqueeCard.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        recyclerView.scrollToPosition(0);
        refreshLayout.autoRefresh(10, 300, 1, false);
    }
}
