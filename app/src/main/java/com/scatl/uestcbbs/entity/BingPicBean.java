package com.scatl.uestcbbs.entity;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

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

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ImagesBean) {
                return Objects.equals(this.url, ((ImagesBean) obj).url)
                        && Objects.equals(this.title, ((ImagesBean) obj).title);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 31 + url.hashCode();
            result = result * 31 + title.hashCode();
            return result;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BingPicBean) {
            return Objects.equals(this.images, ((BingPicBean)obj).images);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + images.hashCode();
        return result;
    }
}
