package com.scatl.uestcbbs.entity;

import android.widget.LinearLayout;

import java.util.List;

/**
 * author: sca_tl
 * date: 2021/3/9 19:59
 * description:
 */
public class ForumDetailBean {
    public int todayPosts;
    public int totalPosts;
    public int rank;
    public List<Admin> admins;

    public static class Admin {
        public String name;
        public int uid;
        public String avatar;
    }
}
