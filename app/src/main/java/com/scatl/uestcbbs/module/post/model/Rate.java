package com.scatl.uestcbbs.module.post.model;

import android.util.Log;

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
 * date: 2020/2/18 15:18
 */
public class Rate {
    private static final String TAG = "Rate";
    public String info;
    public boolean successful;
    public boolean cancelDialog;

    public static Rate rate(String html) {
        Rate rate = new Rate();

        Document doc = null;
        try {
            doc = Jsoup.parse(html);

            if (doc.text().equals("redirect to mobile view")) {
                rate.info = "评分成功";
                rate.cancelDialog = true;
                rate.successful = true;
                return rate;
            }

            /*
             * <script>
             *       alert("帖子不存在或不能被推送");
             *       location.href = "...";
             * </script>
             */
            {
                Pattern pattern = Pattern.compile("alert\\(\"([\\s\\S]*)\"\\)");
                Elements trs = doc.select("script");

                for (Element element: trs) {
                    Matcher matcher = pattern.matcher(element.data());
                    if (matcher.find()) {
                        rate.info = matcher.group(1);
                        rate.cancelDialog = true;
                        rate.successful = false;

                        return rate;
                    }
                }
            }

            /*
             * 例如请输入正确的分值，然后可以继续投分
             */
            {
                Elements trs = doc.select("script");
                Pattern pattern = Pattern.compile("errorMsg = \'([\\s\\S]*)\'");

                for (Element element: trs) {
                    Matcher matcher = pattern.matcher(element.data());
                    if (matcher.find()) {
                        rate.successful = false;
                        rate.info = matcher.group(1);
                        rate.cancelDialog = false;

                        return rate;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (doc != null) {
                Log.e(TAG, "error in poarse:" + doc.text());
            }
        }
        return rate;

    }
}
