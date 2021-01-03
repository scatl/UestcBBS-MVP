package com.scatl.uestcbbs.entity;

import java.io.Serializable;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/19 19:52
 */

public class UpdateBean implements Serializable {

    public Integer returnCode;
    public String returnMsg;
    public UpdateInfoBean updateInfo;

    public static class UpdateInfoBean implements Serializable {
        public String apkMD5;
        public String apkName;
        public String apkSize;
        public String apkUrl;
        public Integer apkVersionCode;
        public String apkVersionName;
        public Integer id;
        public Boolean isForceUpdate;
        public Boolean isValid;
        public Integer releaseDate;
        public String title;
        public String updateContent;
        public String webDownloadUrl;
        public List<String> apkImages;
    }
}
