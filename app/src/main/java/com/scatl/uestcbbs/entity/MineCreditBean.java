package com.scatl.uestcbbs.entity;

import java.util.List;

public class MineCreditBean {

    public String shuiDiNum;
    public String weiWangNum;
    public String jiangLiQuanNum;
    public String jiFenNum;

    public List<CreditHistoryBean> historyBeans;

    public static class CreditHistoryBean {
        public String action;
        public String change;
        public String detail;
        public String time;
        public String link;
        public boolean increase;
    }

}
