package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/10/7 11:26
 * description:
 */
public class OnLineUserBean {

    public int totalUserNum;
    public int totalRegisteredUserNum;
    public int totalVisitorNum;

    public List<UserBean> userBeans;

    public static class UserBean {
        public String userName;
        public String userAvatar;
        public int uid;
        public String time;
    }


}
