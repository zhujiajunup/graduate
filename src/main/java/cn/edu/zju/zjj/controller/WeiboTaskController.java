/**
 * @(#)WeiboTaskController.java, 2018/1/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.constant.ResponseCode;
import cn.edu.zju.zjj.entity.WeiboTask;
import cn.edu.zju.zjj.service.JedisService;
import cn.edu.zju.zjj.service.WeiboTaskService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Slf4j
@RestController
@RequestMapping(value = "/task", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WeiboTaskController {
    private WeiboTaskService taskService;

    private JedisService jedisService;

    @Autowired
    public WeiboTaskController(WeiboTaskService service,
        JedisService jedisService) {
        this.taskService = service;
        this.jedisService = jedisService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean insert(@RequestBody WeiboTask weiboTask) {
        log.info("{}", weiboTask);
        ResponseBean bean = new ResponseBean();
        weiboTask.setStatus("运行中");
        weiboTask.setCreateTime(new Date().getTime());
        if (this.taskService.exist(weiboTask.getWeiboId())) {
            bean.setCode(ResponseCode.ILLEGAL_ACTION.code);
            bean.setMsg(
                "weibo id: " + weiboTask.getWeiboId() + " already added");
        } else {
            this.jedisService.pushToList("comment",
                new HashMap<String, Object>() {
                    {
                        put("url", "https://weibo.cn/comment/"
                            + weiboTask.getWeiboId());
                        put("tweetId", weiboTask.getWeiboId());
                    }
                });
            this.taskService.insert(weiboTask);
        }
        return bean;
    }

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getAll() {

        ResponseBean bean = new ResponseBean();
        List<WeiboTask> tasks = this.taskService.getAll();
        bean.setData(tasks);
        return bean;
    }
}
