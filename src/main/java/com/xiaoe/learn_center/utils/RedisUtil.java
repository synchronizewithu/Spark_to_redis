package com.xiaoe.learn_center.utils;

import com.xiaoe.learn_center.constant.Constant;
import redis.clients.jedis.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RedisUtil implements Serializable {
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static final int MAX_ACTIVE = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static final int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static final int MAX_WAIT = 10000;

    private static final int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, Constant.REDIS_HOST, Constant.REDIS_PORT, TIMEOUT, Constant.REDIS_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 模糊匹配
     *
     * @param pattern key的正则表达式
     * @param count   每次扫描多少条 记录，值越大消耗的时间越短，但会影响redis性能。建议设为一千到一万
     * @return 匹配的key集合
     */
    public static List<String> scan(Jedis jedis, String pattern, int count) {
        List<String> list = new ArrayList<>();
        if (jedis == null) {
            return list;
        }
        try {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams();
            scanParams.count(count);
            scanParams.match(pattern);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                list.addAll(scanResult.getResult());
                cursor = scanResult.getStringCursor();
            } while (!"0".equals(cursor));
            return list;
        } catch (Exception e) {
            jedis.close();
            return list;
        } finally {
            jedis.close();
        }
    }

    /**
     * 记录删除apiLog数据数
     */
    public static void addDeleteApiLogNum() {
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        jedis.incrBy("deleteApiLogNum", 1);
        jedis.close();
    }

    /**
     * 记录删除Mysql数据数
     */
    public static void addDeleteMysqlDataNum() {
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        jedis.incrBy("deleteMysqlDataNum", 1);
        jedis.close();
    }


    public static void addTodayLearnTime(String day, String filed, int st) {
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        System.out.println(" add learn time !! day = " + day);
        String key = "learnRecord" + day;
        jedis.hget(key, filed);
        jedis.hincrBy(key, filed, st);
        jedis.expire(key,Constant.REDIS_EXPIRE_TIME);
        jedis.close();
    }
}
