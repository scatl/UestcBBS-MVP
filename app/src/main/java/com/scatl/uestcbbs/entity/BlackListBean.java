package com.scatl.uestcbbs.entity;

import org.litepal.crud.LitePalSupport;

/**
 * author: sca_tl
 * date: 2020/11/27 18:23
 * description: 本地黑名单数据
 */
public class BlackListBean extends LitePalSupport {
    public int id;
    public String userName;
    public int uid;
    public String avatar;
    public long blackTime;
}
