package com.scatl.uestcbbs.custom.emoticon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.util.CommonUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmoticonPanelLayout extends RelativeLayout {

    private LayoutInflater inflater;
    private LinearLayout root_layout;

    private ViewPager viewPager;
    private MagicIndicator indicator;

    private View emotion_btn;
    private View focus_view;
    private View parent_view;

    public EmoticonPanelLayout(Context context) {
        super(context);
    }

    public EmoticonPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public EmoticonPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        inflater = LayoutInflater.from(getContext());
        RelativeLayout root_view = (RelativeLayout) inflater.inflate(R.layout.view_emoticon_panel_layout, new RelativeLayout(getContext()));
//        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, DeviceConfig.KEYBOARD_HEIGHT);
//        root_view.setLayoutParams(layoutParams);
        viewPager = root_view.findViewById(R.id.view_emoticon_panel_pager);
        indicator = root_view.findViewById(R.id.view_emoticon_panel_indicator);
        initEmoticonPanel();

        addView(root_view);
    }


    /**
     * author: sca_tl
     * description: 指示器
     */
    private void initEmoticonPanel() {

        final List<View> gridViewList = new ArrayList<>();
        final List<String> title_img_path = new ArrayList<>();

        try {

            for (int i = 0; i < 8; i ++) {

                List<String> img_path = new ArrayList<>();
                String[] s = getContext().getAssets().list("emotion/" + (i + 1));

                for (int j = 0; j < s.length; j ++) {
                    img_path.add("file:///android_asset/emotion/" + (i + 1) + "/" + s[j]);
                }

                GridView gridView = new GridView(getContext());
                gridView.setVerticalSpacing(CommonUtil.dip2px(getContext(), 20));
                gridView.setHorizontalSpacing(CommonUtil.dip2px(getContext(), 20));
                gridView.setNumColumns(CommonUtil.screenDpWidth(getContext()) / 60);
                gridView.setAdapter(new EmoticonGridViewAdapter(getContext(), img_path));
                gridView.setVerticalScrollBarEnabled(false);
                gridViewList.add(gridView);
                title_img_path.add(img_path.get(0));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new EmoticonPagerAdapter(gridViewList));
        viewPager.setCurrentItem(0);

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() { return title_img_path.size(); }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                EmoticonPagerTitle emoticonPagerTitle = new EmoticonPagerTitle(context);
                emoticonPagerTitle.setImageSource(title_img_path.get(i));
                emoticonPagerTitle.setOnClickListener(v -> viewPager.setCurrentItem(i));
                return emoticonPagerTitle;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {return null;}
        });

        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);
    }

}
