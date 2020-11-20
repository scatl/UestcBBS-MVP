package com.scatl.uestcbbs.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/10/7 18:55
 * description:
 */
public class MedalBean {

    public List<MedalCenterBean> medalCenterBeans;
    public List<MedalHistoryBean> medalHistoryBeans;

    public static class MedalCenterBean {
        public String medalName;
        public String medalDsp;
        public int medalId;
        public String medalIcon;
        public String buyDsp;
    }

    public static class MedalHistoryBean {
        public String userName;
        public int userId;
        public String userAvatar;
        public String dsp;
    }
}
