package com.scatl.uestcbbs.util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 20:09
 */
public class TimeUtil {
    public static String getFormatDate(long  milliSecond,  String pattern){
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.CHINA);
        return df.format(milliSecond);
    }


    public static long getMilliSecond(String formatDate, String pattern){
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.CHINA);
            Date date = df.parse(formatDate);
            if (date != null) calendar.setTime(date);
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getStringMs() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static Long getLongMs() {
        return System.currentTimeMillis();
    }

    /**
     * author: TanLei
     * description: 获取明天0点
     */
    public static long getTomorrowStartMsTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    /**
     * author: sca_tl
     * description:
     */
    public static String formatTime(String time, int strRes, Context context) {
        long time_ = System.currentTimeMillis() - Long.valueOf(time);
        if (time_ < 3600000) {
            return context.getResources().getString(strRes, (int)Math.floor((double) time_/60000)+"", "分钟");
        } else if (time_ < 86400000) {
            return context.getResources().getString(strRes, (int)Math.floor((double) time_/3600000)+"", "小时");
        } else {
            return context.getResources().getString(strRes, (int)Math.floor((double) time_/86400000)+"", "天");
        }
    }

}
