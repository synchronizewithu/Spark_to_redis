package com.xiaoe.learn_center.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description : 存取偏移量工具类<br/>
 * Copyright(c) , 2019 , Tsing <br/>
 * This program is protected by copyright laws<br/>
 * Date 2019年10月21日
 *
 * @author 商庆浩
 * @version: 1.0
 */
public class DealWithOffset {

    private static Logger logger = LoggerFactory.getLogger(DealWithOffset.class);

    /**
     *
     * @param kafkaStream
     */
    public static void saveOffsetsByStream(String groupId, JavaInputDStream<ConsumerRecord<String, String>> kafkaStream) {
        kafkaStream.foreachRDD(record -> {
            //维护offsets
            if (!record.isEmpty()){
                logger.info("维护offsets.....");
                OffsetRange[] offsetRanges = ((HasOffsetRanges) record.rdd()).offsetRanges();
                saveOffsets(groupId,offsetRanges);
            }
        });
    }

    /**
     * 保存偏移量
     *
     * @param groupId
     * @param offsetRanges
     */
    private static void saveOffsets(String groupId, OffsetRange[] offsetRanges) {

        String preKey = "";

        Jedis jedis = RedisUtil.getJedis();

        for (OffsetRange offsetRange : offsetRanges) {

            //获取当前批次的偏移量
            String topic = offsetRange.topic();
            int partition = offsetRange.partition();
            long untilOffset = offsetRange.untilOffset();

            logger.info("learnCenter:"+groupId + " topic:" + topic + " partition:" + partition + " untilOffset:" + untilOffset);

            preKey = "learnCenter:" + groupId+":"+topic+":"+partition;

            jedis.set(preKey,String.valueOf(untilOffset));
        }

        jedis.close();
    }

    /**
     *  从redis查询offset
     *
     * @param groupId
     * @param topic
     * @return
     */
    public static Map<TopicPartition, Long> getOffsetsByGroupIdAndTopic(String groupId, String topic) throws InterruptedException {

        Map<TopicPartition,Long> returnOffset =  new HashMap<TopicPartition,Long>();
        Jedis jedis = RedisUtil.getJedis();
        String keyPattern = "learnCenter:"+groupId+":"+topic+":*";
        Set<String> keys = jedis.keys(keyPattern);

        for (String key : keys) {
            //在key中找到parition
            String[] split = key.split(":");
            int partition = Integer.valueOf(split[split.length-1]);
            String s = jedis.get(key);
            if (null != s && !"".equals(s)){
                returnOffset.put(new TopicPartition(topic,partition),Long.valueOf(s));
            } else {
                returnOffset.put(new TopicPartition(topic,partition),Long.valueOf("0"));
            }
            logger.info("the offset by redis  key:{},value:{}",key,s);
        }
        jedis.close();
        return returnOffset;
    }
}
