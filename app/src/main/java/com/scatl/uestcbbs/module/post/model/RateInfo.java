package com.scatl.uestcbbs.module.post.model;

import android.util.Log;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description: from https://github.com/yiyuanliu/Hepan
 * date: 2020/2/18 15:19
 */
public class RateInfo {


    public RateInfo(boolean success, String errorReason, int minScore, int maxScore, int todayTotal, String rateUrl) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.todayTotal = todayTotal;
        this.rateUrl = rateUrl;
        this.success = success;
        this.errorReason = errorReason;
    }

    public int minScore;
    public int maxScore;
    public int todayTotal;
    public String rateUrl;
    public boolean success;
    public String errorReason;


    public static RateInfo loadRateInfo(String html) {
        try {

            Document doc = Jsoup.parse(html);

            Elements s = doc.select("script");
            Pattern pattern = Pattern.compile("alert\\(\"([\\s\\S]*)\"\\)");
            for (Element element: s) {
                Matcher matcher = pattern.matcher(element.data());
                if (matcher.find()) {
                    return new RateInfo(false, matcher.group(1) == null ? "未知错误" : matcher.group(1),
                            0, 0, 0, "");
                    //Log.e("ppp", matcher.group(1));
                }
            }

            Element element = doc.select("#rateform").first();
            String rateUrl = element.attr("action");

            Elements trs = doc.select("tr");
            Elements tds = trs.get(1).select("td");

            String range = tds.get(2).text().replace(" ", "");
            String total = tds.get(3).text().trim();
            int totalNum = Integer.parseInt(total);
            int index = range.indexOf("~");
            int min = Integer.parseInt(range.substring(0, index));
            int max = Integer.parseInt(range.substring(index + 1));

            return new RateInfo(true, "", min, max, totalNum, rateUrl);
        } catch (Exception ex) {
            Log.e("Rate", ex.getMessage(), ex);
        }
        return null;
    }

//    private boolean check(Document doc) {
//        Elements trs = doc.select("script");
//
//        Pattern pattern = Pattern.compile("alert\\(\"([\\s\\S]*)\"\\)");
//
//        for (Element element: trs) {
//            Matcher matcher = pattern.matcher(element.data());
//            if (matcher.find()) {
//
//                Log.e("ppp", matcher.group(1));
//            }
//        }
//    }

}
