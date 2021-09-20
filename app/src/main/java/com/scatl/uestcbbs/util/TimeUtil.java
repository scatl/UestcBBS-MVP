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

    public static boolean isCurrentYear(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);

        return currentYear == year;
    }


    /**
     * @param time 时间
     * @param strRes 时间格式资源id
     * @param context 上下文
     * @return 格式化时间
     */
    public static String formatTime(String time, int strRes, Context context) {
        try {

            long d = System.currentTimeMillis() - Long.parseLong(time) + 1000;

            if (d <= 0) {

                return getFormatDate(Long.parseLong(time), "yyyy/MM/dd HH:mm") + "结束";

            } else if (d < 3600000) {

                return context.getResources().getString(strRes, (int)Math.floor((double) d / 60000)+"", "分钟");

            } else if (d < 86400000) {

                return context.getResources().getString(strRes, (int)Math.floor((double) d / 3600000)+"", "小时");

            } else {

                return getFormatDate(Long.parseLong(time), isCurrentYear(Long.parseLong(time)) ? "MM/dd HH:mm" : "yyyy/MM/dd HH:mm");

            }

        } catch (Exception e) {
            return "null";
        }
    }

    /**
     * @description: 计算天数
     * @param formatTime
     * @param pattern
     * @return: int
     */
    public static long caclDays(String formatTime, String pattern) {
        return (TimeUtil.getLongMs() - getMilliSecond(formatTime, pattern)) / 1000 / 60 / 60 / 24;
    }

    public static String getFormatTime(long milliseconds) {
        long minutes = milliseconds / 1000 / 60;
        long seconds = milliseconds / 1000 % 60;

        if (minutes < 60) {
            return String.format(Locale.CHINA, "%02d", minutes) + ":" +
                    String.format(Locale.CHINA, "%02d", seconds);
        } else {
            long hours = minutes / 60;
            minutes %= 60;
            return String.format(Locale.CHINA, "%02d", hours) + ":" +
                    String.format(Locale.CHINA, "%02d", minutes) + ":" +
                    String.format(Locale.CHINA, "%02d", seconds);
        }
    }
}
