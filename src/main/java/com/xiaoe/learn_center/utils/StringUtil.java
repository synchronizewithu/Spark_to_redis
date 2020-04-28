package com.xiaoe.learn_center.utils;

import com.alibaba.fastjson.JSONObject;


public class StringUtil {

    /**
     * 判断字符串是否是json
     *
     * @param str
     * @return
     */
    public static Boolean isJson(String str) {
        try {
            JSONObject json = JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
