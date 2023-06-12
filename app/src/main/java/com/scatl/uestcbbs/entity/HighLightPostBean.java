package com.scatl.uestcbbs.entity;

import android.content.pm.LabeledIntent;

import java.util.List;

/**
 * Created by sca_tl at 2023/6/12 16:28
 */
public class HighLightPostBean {

    public List<Data> mData;

    public static class Data {
        public String title;
        public int tid;
    }

}
