package com.scatl.uestcbbs.entity;

/**
 * author: sca_tl
 * date: 2020/5/17 12:34
 * description:
 */
public class UserGroupBean {
    public String currentLevelStr;//Lv.1
    public String nextLevelStr;
    public int currentLevelNum;
    public int nextLevelNum;
    public int currentCredit;
    public int nextCredit;
    public boolean specialUser;//特殊用户组，不显示Lv
    public boolean topLevel;//Lv.??
    public String totalLevelStr;//蝌蚪 (Lv.1)
    public boolean isAlumna;//校友
}
