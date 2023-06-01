package com.scatl.uestcbbs.entity;

/**
 * 帖子点评
 */
public class PostDianPingBean {
    public boolean hasNext;
    public java.util.List<PostDianPingBean.List> list;

    public static class List {
        public int uid;
        public String userName;
        public String userAvatar;
        public String comment;
        public String date;
    }

}
