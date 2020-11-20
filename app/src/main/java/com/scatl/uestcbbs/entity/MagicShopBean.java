package com.scatl.uestcbbs.entity;

import java.util.List;

public class MagicShopBean {

    public List<ItemList> itemLists;

    public static class ItemList {
        public String icon;
        public String name;
        public String dsp;
        public String price;
        public String id;
    }


}
