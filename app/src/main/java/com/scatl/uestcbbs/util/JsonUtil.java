package com.scatl.uestcbbs.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/15 19:18
 */
public class JsonUtil {

    /**
     * author: sca_tl
     * description: 将实体A中和实体B相同的属性赋给实体B
     */
    public static <A, B> B modelA2B(A modelA, Class<B> bClass) {
        Gson gson = new Gson();
        String s = gson.toJson(modelA);
        return gson.fromJson(s, bClass);
    }

    /**
     * author: sca_tl
     * description: 实体数组
     */
    public static <A, B> List<B> modelListA2B(List<A> modelAList, Class<B> modelBClass, int targetSize) {
        List<B> bList = new ArrayList<>();
        for (int i = 0; i < (Math.min(targetSize, modelAList.size())); i ++) {
            bList.add(modelA2B(modelAList.get(i), modelBClass));
        }
        return bList;
    }

}
