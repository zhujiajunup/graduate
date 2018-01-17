/**
 * @(#)JedisServiceTest.java, 2018/1/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj;

import cn.edu.zju.zjj.service.JedisService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
public class JedisServiceTest {
    public static void main(String [] args){
        JedisService service = new JedisService();
        Map<String, Object> info = new HashMap<>();
        info.put("user_id", "6400792580");
        service.pushToList("user", info);
        //System.out.println(service.getFromList("user"));
    }
}