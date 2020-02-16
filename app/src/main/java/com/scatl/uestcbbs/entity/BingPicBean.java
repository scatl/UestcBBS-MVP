package com.scatl.uestcbbs.entity;

import java.util.List;

public class BingPicBean {

    public TooltipsBean tooltips;
    public List<ImagesBean> images;

    public static class TooltipsBean {
        public String loading;
        public String previous;
        public String next;
        public String walle;
        public String walls;
    }

    public static class ImagesBean {
        public String startdate;
        public String fullstartdate;
        public String enddate;
        public String url;
        public String urlbase;
        public String copyright;
        public String copyrightlink;
        public String title;
        public String quiz;
        public boolean wp;
        public String hsh;
        public int drk;
        public int top;
        public int bot;
        public List<?> hs;
    }
}
