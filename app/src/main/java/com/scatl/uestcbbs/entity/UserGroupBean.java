package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * date: 2020/5/17 12:34
 * description:
 */
public class UserGroupBean {
    public String currentLevelStr;
    public String nextLevelStr;
    public int currentLevelNum;
    public int nextLevelNum;
    public int currentCredit;
    public int nextCredit;
    public boolean specialUser;//特殊用户组，不显示Lv
    public boolean topLevel;//Lv.??
}
