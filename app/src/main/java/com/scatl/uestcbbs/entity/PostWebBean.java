package com.scatl.uestcbbs.entity;

import java.util.List;

public class PostWebBean {
    public String favoriteNum;
    public String rewardInfo;
    public String shengYuReword;
    public String formHash;
    public boolean originalCreate;
    public boolean essence;
    public boolean topStick;
    public int supportCount;
    public int againstCount;
    public String actionHistory;//帖子操作历史
    public String modifyHistory;
    public boolean isWarned;
    public List<Collection> collectionList;
    public PostDianPingBean dianPingBean;

    public static class Collection {
        public String name;
        public int ctid;
        public String subscribeCount;
    }
}
