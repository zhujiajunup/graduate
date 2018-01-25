/**
 * @(#)WeiboTaskService.java, 2018/1/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.edu.zju.zjj.service;

import org.apache.zookeeper.Op;
import org.springframework.stereotype.Service;

import cn.edu.zju.zjj.App;
import cn.edu.zju.zjj.dao.WeiboTaskDao;
import cn.edu.zju.zjj.dao.WeiboTweetDao;
import cn.edu.zju.zjj.dao.WeiboUserDao;
import cn.edu.zju.zjj.entity.WeiboTask;
import cn.edu.zju.zjj.entity.WeiboTweet;
import cn.edu.zju.zjj.entity.WeiboUser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 祝佳俊(hzzhujiajun@corp.netease.com)
 */
@Slf4j
@Service
public class WeiboTaskService {
    private WeiboTaskDao taskDao;

    private WeiboUserDao userDao;

    private WeiboTweetDao tweetDao;

    public WeiboTaskService(WeiboTaskDao dao, WeiboUserDao userDao,
        WeiboTweetDao tweetDao) {
        this.taskDao = dao;
        this.userDao = userDao;
        this.tweetDao = tweetDao;

    }

    public boolean exist(String weiboId) {
        return this.taskDao.exist(weiboId);
    }

    public void insert(WeiboTask task) {
        this.taskDao.insert(task);
    }

    public List<WeiboTask> getAll() {
        Optional<List<WeiboTask>> tasksOpt = Optional.ofNullable(this.taskDao.getAll());
        List<WeiboTask> tasks = tasksOpt.orElse(new ArrayList<>());
        tasks.stream().filter(weiboTask -> weiboTask.getWeiboContent() == null)
            .forEach(weiboTask -> App.threadPool.submit(() -> {
                log.info("update for task: {}", "M_"+weiboTask.getWeiboId());
                Optional<WeiboTweet> tweetOpt = Optional
                    .ofNullable(this.tweetDao.getById("M_"+weiboTask.getWeiboId()));
                if (tweetOpt.isPresent()) {
                    Optional<WeiboUser> userOpt = Optional
                        .ofNullable(this.userDao
                            .getById(String.valueOf(tweetOpt.get().getUid())));
                    if (userOpt.isPresent()) {
                        log.info("get user {}", userOpt.get());
                        weiboTask.setWeiboContent(tweetOpt.get().getContent());
                        weiboTask.setWeiboUser(userOpt.get().getNickname());
                        this.taskDao.update(weiboTask);
                    }
                }
            })

        );
        return tasks;
    }
}
