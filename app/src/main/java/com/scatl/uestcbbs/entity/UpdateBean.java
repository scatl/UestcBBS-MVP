package com.scatl.uestcbbs.entity;

import java.io.Serializable;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/19 19:52
 */
public class UpdateBean implements Serializable {
    public String title;
    public String releaseDate;
    public String apkName;
    public String apkUrl;
    public String MD5;
    public String apkSize;
    public int versionCode;
    public String versionName;
    public String updateContent;
    public boolean isForceUpdate;
}
