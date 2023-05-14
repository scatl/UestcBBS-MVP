package com.scatl.uestcbbs.util;

import android.content.Context;

import com.scatl.uestcbbs.callback.OnRefresh;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/07 14:34
 */
public class RefreshUtil {

    public static void setOnRefreshListener(Context context, SmartRefreshLayout refreshLayout, final OnRefresh onRefresh) {
        ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在拼命加载";
        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载成功😀";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "哦豁，加载失败🙁";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "啊哦，没有更多数据了";

        refreshLayout.setRefreshFooter(new ClassicsFooter(context)
                .setDrawableArrowSize(14))
                .setFooterHeight(30);
        refreshLayout.setEnableOverScrollBounce(false); //关闭越界回弹功能
        refreshLayout.setRefreshHeader(new MaterialHeader(context).setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)).setReboundDuration(300);

        refreshLayout.setEnableAutoLoadMore(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);

        refreshLayout.setOnRefreshListener(onRefresh::onRefresh);
        refreshLayout.setOnLoadMoreListener(onRefresh::onLoadMore);

    }

}
