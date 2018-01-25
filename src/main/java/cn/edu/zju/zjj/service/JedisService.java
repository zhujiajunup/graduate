/**
 * @(#)JedisService.java, 2018/1/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.service;

import cn.edu.zju.zjj.util.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Slf4j
@Service
public class JedisService {

    private JedisUtil jedisUtil;

    private String jedisHost = "localhost";

    private int jedisPort = 6379;

    private int db = 1;

    private ObjectMapper mapper = new ObjectMapper();

    public JedisService() {
        jedisUtil = JedisUtil.getInstance();
    }

    public void pushToList(String key, Map<String, Object> value) {
        log.info("put to list, key: {}, value: {}", key, value);
        try (Jedis jedis = jedisUtil.getJedis(jedisHost, jedisPort, db)) {

            jedis.lpush(key, mapper.writeValueAsString(value));

        } catch (JsonProcessingException e) {
            log.error("", e);
        }

    }

    public String getFromList(String key) {
        try (Jedis jedis = jedisUtil.getJedis(jedisHost, jedisPort, db)) {
            return jedis.lpop(key);
        }
    }

}
