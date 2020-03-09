package com.scatl.uestcbbs.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * author: sca_tl
 * description:
 * date: 2020/3/3 14:16
 */
public class ReplyDraftBean extends LitePalSupport implements Serializable {
    public int id;
    public int reply_id;
    public String content;
    public String images;
}
