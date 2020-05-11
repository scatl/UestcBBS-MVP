package com.scatl.uestcbbs.entity;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/1 21:26
 * description:
 */
@XStreamAlias("rss")
public class GrabSofaBean {

    @XStreamAsAttribute()
    @XStreamAlias("version")
    public String version;

    public ChannelBean channel;

    public static class ChannelBean {

        public String title;
        public String link;
        public String description;
        public String copyright;
        public String generator;
        public String lastBuildDate;
        public String ttl;
        public ImageBean image;
        @XStreamImplicit(itemFieldName = "item")
        public List<ItemBean> itemBeans;

        public static class ImageBean {
            public String url;
            public String link;
            public String title;
        }

        public static class ItemBean {
            public String title;
            public String link;
            public String description;
            public String category;
            public String pubDate;
            public String author;

            @XStreamImplicit(itemFieldName = "enclosure")
            public List<Enclosure> enclosure;

            @XStreamAlias("enclosure")
            public static class Enclosure {
                @XStreamAsAttribute()
                public String url;
                @XStreamAsAttribute()
                public String length;
                @XStreamAsAttribute()
                public String type;
            }
        }

    }

}
