package com.xiaoe.learn_center.constant;


public class Constant {
    //开发环境kafka参数
    public final static String KAFKA_TOPIC_LEARN_CENTER = "learn_center_push";
    public final static String KAFKA_OFFSET_RESET = "latest";
    public final static String KAFKA_BOOTSTRAP_SERVERS = "203.195.219.253:9092";
    public final static String KAFKA_GROUP_ID = "learncentertest";
    public final static boolean KAFKA_AUTO_COMMIT = false;

    //开发环境redis
    public final static String REDIS_HOST = "10.0.0.5";
    public final static int REDIS_PORT = 6379;
    public final static String REDIS_PASSWORD = "crs-6m5ati3t:redis@1111";
    public final static int REDIS_EXPIRE_TIME = 259200;
    //kafka消费参数
//    public final static String KAFKA_TOPIC_API_LOG = "buz";
//    public final static String KAFKA_OFFSET_RESET = "latest";
//    public final static String KAFKAORDER_BOOTSTRAP_SERVERS = "10.0.1.3:9092";
//    public final static String KAFKA_GROUP_ID = "cuseretl";
//    public final static boolean KAFKA_AUTO_COMMIT = false;

    //redis参数
//    public final static String REDIS_HOST = "10.0.0.6";
//    public final static int REDIS_PORT = 6379;
//    public final static String REDIS_PASSWORD = "crs-502kw4g3:data@0816";
//    public final static int REDIS_EXPIRE_TIME = 259200;

    //spark参数
    public final static String SPARK_APPNAME = "learn_center_realtime";
    public final static Long SPARK_STREAMING_DURATION = 3L;

}
