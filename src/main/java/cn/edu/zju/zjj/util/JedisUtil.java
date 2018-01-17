/**
 * @(#)JedisUtil.java, 2018/1/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Slf4j
public class JedisUtil {
    private static Map<String, JedisPool> jedisPoolCache = new ConcurrentHashMap<>();

    private JedisUtil() {}

    private static class Holder {
        private static final JedisUtil INSTANCE = new JedisUtil();
    }

    public static JedisUtil getInstance(){
        return Holder.INSTANCE;
    }

    private static JedisPool getPool(String ip, int port, int db) {
        String key = ip + ":" + port + "/" + db;
        JedisPool pool;
        if (!jedisPoolCache.containsKey(key)) {
            JedisPoolConfig config = new JedisPoolConfig();

            config.setMaxIdle(RedisConfig.MAX_IDLE);
            config.setMaxWaitMillis(RedisConfig.MAX_WAIT);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);

            pool = new JedisPool(config, ip, port, RedisConfig.TIMEOUT, null, db, null);
            jedisPoolCache.put(key, pool);
        } else {
            pool = jedisPoolCache.get(key);
        }
        return pool;
    }

    public Jedis getJedis(String ip, int port, int db) {
        Jedis jedis;
        int count = 0;
        do {
            jedis = getPool(ip, port, db).getResource();
        } while (jedis == null && count < RedisConfig.RETRY_NUM);
        return jedis;
    }

    static class RedisConfig {
        //可用连接实例的最大数目，默认值为8；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        public static int MAX_ACTIVE = 1024;

        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
        private static int MAX_IDLE = 200;

        //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
        private static int MAX_WAIT = 10000;

        private static int TIMEOUT = 10000;

        private static int RETRY_NUM = 5;
    }
}
