package com.scatl.uestcbbs.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2020/4/6 21:06
 */
public class HistoryBean extends LitePalSupport implements Serializable {
    public int id;
    public int board_id;
    public String board_name;
    public int topic_id;
    public String title;
    public int user_id;
    public String last_reply_date;
    public String user_nick_name;
    public int hits;
    public String subject;
    public int replies;
    public String userAvatar;
    public int recommendAdd;
    public long browserTime;
}
