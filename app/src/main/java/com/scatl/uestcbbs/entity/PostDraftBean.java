package com.scatl.uestcbbs.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 19:58
 */
public class PostDraftBean extends LitePalSupport implements Serializable {
    public int id;
    public long time;
    public String title;
    public String content;
    public String board_name;
    public String filter_name;
    public int board_id;
    public int filter_id;
    public String content_summary;
    public String image_summary;
    public String poll_options;
    public int poll_exp;
    public int poll_choices;
    public boolean poll_visible;
    public boolean poll_show_voters;
    public boolean anonymous;
    public boolean only_user;
    public boolean isSanShui;
}
