package com.scatl.uestcbbs.entity;

import java.util.List;

public class MineMagicBean {
    public List<MineMagicBean.ItemList> itemLists;

    public static class ItemList {
        public String icon;
        public String name;
        public String dsp;
        public String totalCount;
        public String totalWeight;
        public String magicId;
        public boolean showUseBtn;
    }
}
