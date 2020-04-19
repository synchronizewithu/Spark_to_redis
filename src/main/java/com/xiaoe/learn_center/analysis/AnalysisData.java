package com.xiaoe.learn_center.analysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.learn_center.utils.RedisUtil;
import com.xiaoe.learn_center.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description :计算用户学习数据<br/>
 * Copyright(c) , 2019 , Tsing <br/>
 * This program is protected by copyright laws<br/>
 * Date 2019年10月21日
 *
 * @author 商庆浩
 * @version: 1.0
 */
public class AnalysisData {

    private static Logger logger = LoggerFactory.getLogger(AnalysisData.class);

    public static boolean caculateLog(String str) {

        System.out.println("************** data is : " + str);
        if (!StringUtil.isJson(str)) {
            logger.info("Data is NOT JSON ，DELETE !");
            System.out.println("$$$$$$$$$$$$$$$$$$ Not Json data!");
            return false;
        }
        if (str.isEmpty() || " ".equals(str)) {
            System.out.println("$$$$$$$$$$$$$$$$$$ NUll data!");
            return false;
        }

        //解析json
        JSONObject jsonObject = JSON.parseObject(str);
        String appId = jsonObject.getString("app_id");
        String userId = jsonObject.getString("user_id");
        int stayTime = jsonObject.getIntValue("stay_time");
        String pushedAt = jsonObject.getString("pushed_at");

        if (pushedAt.isEmpty()) {
            System.out.println("$$$$$$$$$$$$$$$$$$ NUll pushedAt!");
            return false;
        }

        String day = pushedAt.substring(0, 10).replaceAll("-", "");

        System.out.println("app_id : " + appId);
        System.out.println("user_id : " + userId);
        System.out.println("stay_time : " + stayTime);
        System.out.println("pushed_at : " + pushedAt);


        String filed = appId + ":" + userId;
        RedisUtil.addTodayLearnTime(day, filed, stayTime);
        return true;
    }

}