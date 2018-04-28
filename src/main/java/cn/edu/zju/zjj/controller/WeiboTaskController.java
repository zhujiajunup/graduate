/**
 * @(#)WeiboTaskController.java, 2018/1/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.controller;

import cn.edu.zju.zjj.bean.ResponseBean;
import cn.edu.zju.zjj.bean.TaskBean;
import cn.edu.zju.zjj.bean.UserTaskBean;
import cn.edu.zju.zjj.constant.ResponseCode;
import cn.edu.zju.zjj.entity.*;
import cn.edu.zju.zjj.service.JedisService;
import cn.edu.zju.zjj.service.TopicTaskService;
import cn.edu.zju.zjj.service.UserTaskService;
import cn.edu.zju.zjj.service.WeiboTaskService;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import scala.Int;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Slf4j
@RestController
@RequestMapping(value = "/task", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WeiboTaskController {
    private WeiboTaskService taskService;

    private JedisService jedisService;

    private UserTaskService userTaskService;

    private TopicTaskService topicTaskService;

    private ThreadLocal<DateFormat> dateFormatThreadLocal = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    @Autowired
    public WeiboTaskController(WeiboTaskService service,
                               UserTaskService userTaskService,
                               TopicTaskService topicTaskService,
                               JedisService jedisService) {
        this.taskService = service;
        this.userTaskService = userTaskService;
        this.jedisService = jedisService;
        this.topicTaskService = topicTaskService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean insert(@RequestBody TaskBean taskBean) {
        log.info("{}", taskBean);
        ResponseBean bean = new ResponseBean();
        Date now = new Date();
        switch (taskBean.getType()) {
            case "user":
                UserTask userTask = new UserTask();
                userTask.setCreateTime(now.getTime());
                userTask.setStatus("运行中");
                try {
                    userTask.setUserId(Long.parseLong(taskBean.getContent()));
                    this.jedisService.pushToList("user",
                            new HashMap<String, Object>() {
                                {
                                    put("user_id", taskBean.getContent());
                                }
                            });
                    this.jedisService.pushToList("tweet",
                            new HashMap<String, Object>() {
                                {
                                    put("url", "https://weibo.cn/"
                                            + taskBean.getContent());
                                    put("uid", taskBean.getContent());
                                }
                            });
                    this.jedisService.pushToList("inc_tweet",
                            new HashMap<String, Object>() {
                                {
                                    put("url", "https://weibo.cn/"
                                            + taskBean.getContent());
                                    put("uid", taskBean.getContent());
                                    put("time", dateFormatThreadLocal.get().format(new Date()));
                                }
                            });
                    this.userTaskService.insert(userTask);
                } catch (NumberFormatException e) {
                    bean.setCode(ResponseCode.ILLEGAL_ARGUMENTS.code);
                    bean.setMsg("用户id输入有误，请输入正确的用户id(" + taskBean.getContent() + ")");
                }
                break;
            case "topic":
                TopicTask topicTask = new TopicTask();
                topicTask.setTopic(taskBean.getContent());
                topicTask.setCreateTime(now.getTime());
                topicTask.setStatus("运行中");
                this.topicTaskService.insert(topicTask);
//                this.jedisService.pushToList("search",
//                        new HashMap<String, Object>() {
//                            {
//                                put("url", "https://weibo.cn/comment/"
//                                        + weiboTask.getWeiboId());
//                                put("tweetId", weiboTask.getWeiboId());
//                            }
//                        });
                break;
            case "tweet":
                WeiboTask weiboTask = new WeiboTask();
                weiboTask.setWeiboId(taskBean.getContent());
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
        }

        return bean;
    }

    @RequestMapping(value = "/tweet/all", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getAllTweetTask() {

        ResponseBean bean = new ResponseBean();
        List<WeiboTask> tasks = this.taskService.getAll();
        bean.setData(tasks);
        return bean;
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getAllUserTask() {

        ResponseBean bean = new ResponseBean();
        List<UserTaskBean> tasks = this.userTaskService.getAll();
        bean.setData(tasks);
        return bean;
    }

    @RequestMapping(value = "/topic/all", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean getAllTopicTask() {

        ResponseBean bean = new ResponseBean();
        List<TopicTask> tasks = this.topicTaskService.getAll();
        bean.setData(tasks);
        return bean;
    }
}
