/**
 * @(#)WeiboTweetController.java, 2018/1/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.service.WeiboTweetService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 祝佳俊(zhujiajunup@163.com)
 */
@Slf4j
@RestController
@RequestMapping(value = "/tweet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WeiboTweetController {
    private WeiboTweetService tweetService;

    public WeiboTweetController(WeiboTweetService tweetService) {
        this.tweetService = tweetService;
    }

    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getTweet(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String tweetId = (String) params.get("tweetId");
        bean.setData(this.tweetService.getById(tweetId));
        return bean;

    }
}
