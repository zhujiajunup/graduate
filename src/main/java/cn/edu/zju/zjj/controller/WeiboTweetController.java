/**
 * @(#)WeiboTweetController.java, 2018/1/4.
 * <p/>
 * Copyright 2018 HEHE, Inc. All rights reserved.
 * HEHE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.service.WeiboTweetService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import scala.Int;

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

    @RequestMapping(value = "/getByUid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getUserTweets(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String uid = (String) params.get("uid");
        bean.setData(this.tweetService.getByUid(uid));
        return bean;
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getByPageg(@RequestBody Map<String, Object> params) {
        log.info("{}", params);
        ResponseBean bean = new ResponseBean();
        String uid = (String) params.get("uid");
        int pageNum = (Integer) params.get("pageNum");
        int pageSize = (Integer) params.get("pageSize");
        int total = this.tweetService.getTotal(uid);
        int maxPage = total / pageSize + (total % pageSize == 0 ? 0 : 1);
        List<WeiboTweet> tweets = this.tweetService.getByPage(uid, pageSize, pageNum);
        Map<String, Object> result = new HashMap<>();
        result.put("tweets", tweets);
        result.put("total", total);
        result.put("maxPage", maxPage);
        result.put("pageSize", pageSize);
        result.put("pageNum", pageNum);
        bean.setData(result);
        return bean;
    }

}
