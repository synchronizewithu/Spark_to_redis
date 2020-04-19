package com.xiaoe.learn_center.main;

import com.xiaoe.learn_center.analysis.AnalysisData;
import com.xiaoe.learn_center.constant.Constant;
import com.xiaoe.learn_center.utils.DealWithOffset;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description :xxx<br/>
 * Copyright(c) , 2019 , Tsing <br/>
 * This program is protected by copyright laws<br/>
 * Date 2019年10月21日
 *
 * @author 商庆浩
 * @version: 1.0
 */
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName(Constant.SPARK_APPNAME);
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, Durations.seconds(Constant.SPARK_STREAMING_DURATION));
        jsc.checkpoint("/ckforlearncenter");

        //配置kafkaorder
        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("bootstrap.servers", Constant.KAFKA_BOOTSTRAP_SERVERS);
        kafkaParams.put("group.id", Constant.KAFKA_GROUP_ID);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("auto.offset.reset", Constant.KAFKA_OFFSET_RESET);
        kafkaParams.put("enable.auto.commit", Constant.KAFKA_AUTO_COMMIT);

        //解决可能的数据丢失和数据重复消费的问题
        //将偏移量保存到redis上面
        //查询现有偏移量
        Map<TopicPartition, Long> learnCenterOffsets = null;
        learnCenterOffsets = DealWithOffset.getOffsetsByGroupIdAndTopic(Constant.KAFKA_GROUP_ID, Constant.KAFKA_TOPIC_LEARN_CENTER);

        JavaInputDStream<ConsumerRecord<String, String>> learnCenterFlowStream;


        if (learnCenterOffsets.isEmpty()) {
            logger.info("---- Api Log Offset is Empty ");
            learnCenterFlowStream =
                    KafkaUtils.createDirectStream(
                            jsc,
                            LocationStrategies.PreferConsistent(),
                            ConsumerStrategies.Subscribe(Arrays.asList(Constant.KAFKA_TOPIC_LEARN_CENTER), kafkaParams)
                    );
            logger.info("---- Api Log Create JavaInputDStream Success ");
        } else {
            logger.info("---- Api Log Get Offset Success ");
            learnCenterFlowStream =
                    KafkaUtils.createDirectStream(
                            jsc,
                            LocationStrategies.PreferConsistent(),
                            ConsumerStrategies.Subscribe(Arrays.asList(Constant.KAFKA_TOPIC_LEARN_CENTER), kafkaParams, learnCenterOffsets)
                    );
            logger.info("---- Api Log Create JavaInputDStream Success ");
        }


        //遍历处理api_log数据
        //如果被抛弃，redis记录丢弃api_log数据+1
        learnCenterFlowStream.map(record -> record.value()).foreachRDD(data -> data.foreachPartition(lines -> {
            logger.info("###### Start Deal Every Api Log Data ######");
            while (lines.hasNext()) {
                AnalysisData.caculateLog(lines.next());
            }
        }));


        //维护流水的偏移量
        DealWithOffset.saveOffsetsByStream(Constant.KAFKA_GROUP_ID, learnCenterFlowStream);

        jsc.start();
        //控制程序停止
        //checkIfStop(filePath,jsc);
        jsc.awaitTermination();
    }
}
