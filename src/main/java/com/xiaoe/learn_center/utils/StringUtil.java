package com.xiaoe.learn_center.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * Description : 字符串工具类<br/>
 * Copyright(c) , 2019 , Tsing <br/>
 * This program is protected by copyright laws<br/>
 * Date 2019年10月21日
 *
 * @author 商庆浩
 * @version: 1.0
 */
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
