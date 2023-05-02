package com.scatl.uestcbbs.util;

import android.content.Context;

import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.BlackListBean;
import com.scatl.util.ColorUtil;

import org.litepal.LitePal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * date: 2020/5/3 16:03
 * description: 从论坛链接获取相关信息
 */
public class ForumUtil {

    public static String getAppHashValue() {
        String timeString = String.valueOf(System.currentTimeMillis());
        String authkey = "appbyme_key";
        String authString = timeString.substring(0, 5) + authkey;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashkey = md.digest(authString.getBytes());
        return new BigInteger(1, hashkey).toString(16).substring(8, 16);//16进制转换字符串
    }

    //获取等级颜色
    public static int getLevelColor(Context context, String userLevel) {
        if (("蝌蚪 (Lv.1)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_1);
        }
        if (("虾米 (Lv.2)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_2);
        }
        if (("河蟹 (Lv.3)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_3);
        }
        if (("泥鳅 (Lv.4)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_4);
        }
        if (("草鱼 (Lv.5)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_5);
        }
        if (("鳙鱼 (Lv.6)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_6);
        }
        if (("鲤鱼 (Lv.7)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_7);
        }
        if (("鲶鱼 (Lv.8)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_8);
        }
        if (("白鳍 (Lv.9)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_9);
        }
        if (("海豚 (Lv.10)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_10);
        }
        if ("鲨鱼 (Lv.11)".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_10);
        }
        if (("逆戟鲸 (Lv.12)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_10);
        }
        if (("传奇蝌蚪 (Lv.??)").equals(userLevel)) {
            return App.getContext().getColor(R.color.level_color_10);
        }

        if ("水藻河泥 (Lv.0 禁言中…)".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_forbidden);
        }
        if ("禁止访问".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_forbidden);
        }
        if ("禁止 IP".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_forbidden);
        }

        if ("管理员".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("超级版主".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("版主".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("实习版主".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("组织机构".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("系统管理员".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_manager);
        }

        if ("清水河畔VIP".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_other_user_group);
        }

        if ("星辰工作室".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_other_user_group);
        }

        if ("退休版主".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_other_user_group);
        }

        if ("Leviathan".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_other_user_group);
        }

        if ("站长".equals(userLevel)) {
            return App.getContext().getColor(R.color.level_bbs_web_master);
        }

        return ColorUtil.getAttrColor(context, R.attr.colorPrimary);
    }

}
